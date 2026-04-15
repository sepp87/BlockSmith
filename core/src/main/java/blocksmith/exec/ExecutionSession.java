package blocksmith.exec;

import blocksmith.app.outbound.AppScheduler;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.PortRef;
import blocksmith.domain.graph.Graph;
import blocksmith.domain.graph.GraphDiff;
import blocksmith.domain.graph.GraphFactory;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joost
 */
public class ExecutionSession {

    private static final Logger LOGGER = Logger.getLogger(ExecutionSession.class.getName());

    private final ExecutionEngine engine;
    private final ExecutionState state;
    private final ExecutionInvalidator invalidator;
    private final SourceBlockIndex sourceBlocks;
    private final AppScheduler scheduler;
    private Graph current;
    private boolean initialRun = true;

    public ExecutionSession(
            ExecutionEngine engine,
            ExecutionState state,
            ExecutionInvalidator invalidator,
            SourceBlockIndex sourceBlockRegistry,
            AppScheduler scheduler,
            Graph graph) {

        this.engine = engine;
        this.state = state;
        this.invalidator = invalidator;
        this.sourceBlocks = sourceBlockRegistry;
        this.scheduler = scheduler;
        this.current = graph;
    }

    public void start() {
        onGraphChanged(GraphFactory.createEmpty(), current);
    }

    public ExecutionState runtimeState() {
        return state;
    }

    public void onGraphChanged(Graph oldGraph, Graph newGraph) {
        scheduler.runInBackground(() -> {
            this.current = newGraph;
            var diff = GraphDiff.compare(oldGraph, newGraph);
            sourceBlocks.updateFrom(diff);
            var shouldRun = invalidator.invalidate(state, oldGraph, newGraph, diff);
            if (shouldRun || initialRun) {
                initialRun = false;
                var start = System.currentTimeMillis();
                engine.runAll(newGraph, state, this::onSourceBlockEmitted);
                LOGGER.log(Level.FINEST, "execution cycle: {0}ms", System.currentTimeMillis() - start);
            }
        });
    }

    public void onSourceBlockEmitted(BlockId block, Map<PortRef, Object> outputs) {
        scheduler.runInBackground(() -> {

            // invalidate downstream
            invalidator.invalidateDownstreamExcluding(state, current, block);

            // set state of source block
            var source = sourceBlocks.get(block).orElseThrow();
            var exception = source.error().isPresent() ? new BlockException(null, BlockException.Severity.ERROR, source.error().get()) : null;
            var exceptions = exception != null ? List.of(exception) : List.<BlockException>of();
            state.updateBlockState(block, outputs, BlockStatus.FINISHED, exceptions);

            // re-run to push source block state
            engine.runAll(current, state, this::onSourceBlockEmitted);
        });
    }

}

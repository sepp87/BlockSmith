package blocksmith.exec;

import blocksmith.domain.block.BlockDef;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.PortRef;
import blocksmith.domain.graph.Graph;
import blocksmith.domain.graph.GraphDiff;
import blocksmith.domain.graph.GraphFactory;
import java.util.List;
import java.util.Map;
/**
 *
 * @author joost
 */
public class ExecutionSession {

    private final ExecutionEngine engine;
    private final ExecutionState state;
    private final ExecutionInvalidator invalidator;
    private final SourceBlockIndex sourceBlocks;
    private Graph current;

    public ExecutionSession(
            ExecutionEngine engine,
            ExecutionState state,
            ExecutionInvalidator invalidator,
            SourceBlockIndex sourceBlockRegistry,
            Graph graph) {

        this.engine = engine;
        this.state = state;
        this.invalidator = invalidator;
        this.sourceBlocks = sourceBlockRegistry;
        this.current = graph;
        onGraphChanged(GraphFactory.createEmpty(), graph);
    }

    public ExecutionState runtimeState() {
        return state;
    }

    public void onGraphChanged(Graph oldGraph, Graph newGraph) {
        this.current = newGraph;
        var diff = GraphDiff.compare(oldGraph, newGraph);
        sourceBlocks.updateFrom(diff);
        invalidator.invalidate(state, oldGraph, newGraph, diff);
        engine.runAll(newGraph, state, this::onSourceBlockEmitted);
    }

    public void onSourceBlockEmitted(BlockId block, Map<PortRef, Object> outputs) {
        
        // invalidate downstream
        invalidator.invalidateDownstreamExcluding(state, current, block);
        
        // set state of source block
        var source = sourceBlocks.get(block).orElseThrow();
        var exception = source.error().isPresent() ? new BlockException(null, BlockException.Severity.ERROR, source.error().get()) : null;
        var exceptions = exception != null ? List.of(exception) : List.<BlockException>of();
        state.updateBlockState(block, outputs, BlockStatus.FINISHED, exceptions);
        
        // re-run to push source block state
        engine.runAll(current, state, this::onSourceBlockEmitted);
        
    }
    
}

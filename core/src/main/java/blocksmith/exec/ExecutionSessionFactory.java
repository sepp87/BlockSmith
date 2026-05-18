package blocksmith.exec;

import blocksmith.app.block.BlockLibrary;
import blocksmith.app.outbound.AppScheduler;
import blocksmith.domain.graph.Graph;
import blocksmith.exec.engine.ExecutionEngine;
import blocksmith.exec.engine.ExecutionInvalidator;
import blocksmith.exec.engine.ExecutionState;
import blocksmith.exec.engine.SourceBlockIndex;
import blocksmith.exec.engine.ValueConverter;
import blocksmith.app.inbound.TypeLookup;

/**
 *
 * @author joost
 */
public class ExecutionSessionFactory {

    private final BlockLibrary blockLibrary;
    private final AppScheduler scheduler;

    public ExecutionSessionFactory(BlockLibrary blockLibrary, AppScheduler scheduler) {
        this.blockLibrary = blockLibrary;
        this.scheduler = scheduler;
    }

    public ExecutionSession create(Graph graph, TypeLookup valueTypeResolver) {
        var sourceBlocks = new SourceBlockIndex(blockLibrary);
        var valueConverter = new ValueConverter(valueTypeResolver);
        var engine = new ExecutionEngine(blockLibrary, valueTypeResolver, valueConverter, sourceBlocks);
        var state = new ExecutionState();
        var invalidator = new ExecutionInvalidator();
        var session = new ExecutionSession(engine, state, invalidator, sourceBlocks, scheduler, graph);
        return session;
    }

}

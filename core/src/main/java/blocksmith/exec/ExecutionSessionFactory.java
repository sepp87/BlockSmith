package blocksmith.exec;

import blocksmith.app.block.BlockLibrary;
import blocksmith.app.outbound.AppScheduler;
import blocksmith.domain.graph.Graph;

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

    public ExecutionSession create(Graph graph) {
        var sourceBlocks = new SourceBlockIndex(blockLibrary);
        var engine = new ExecutionEngine(blockLibrary, sourceBlocks);
        var state = new ExecutionState();
        var invalidator = new ExecutionInvalidator();
        var session = new ExecutionSession(engine, state, invalidator, sourceBlocks, scheduler, graph);
        return session;
    }

}

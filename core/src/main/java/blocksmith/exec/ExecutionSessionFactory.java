package blocksmith.exec;

import blocksmith.app.block.BlockDefLibrary;
import blocksmith.app.block.BlockExecLibrary;
import blocksmith.app.outbound.AppScheduler;
import blocksmith.domain.graph.Graph;

/**
 *
 * @author joost
 */
public class ExecutionSessionFactory {

    private final BlockDefLibrary defLibrary;
    private final BlockExecLibrary execLibrary;
    private final AppScheduler scheduler;

    public ExecutionSessionFactory(BlockDefLibrary defLibrary, BlockExecLibrary execLibrary, AppScheduler scheduler) {
        this.defLibrary = defLibrary;
        this.execLibrary = execLibrary;
        this.scheduler = scheduler;
        
    }

    public ExecutionSession create(Graph graph) {
        var sourceBlocks = new SourceBlockIndex(execLibrary);
        var engine = new ExecutionEngine(defLibrary, execLibrary, sourceBlocks);
        var state = new ExecutionState();
        var invalidator = new ExecutionInvalidator();
        var session = new ExecutionSession(engine, state, invalidator, sourceBlocks, scheduler, graph);
        return session;
    }

}

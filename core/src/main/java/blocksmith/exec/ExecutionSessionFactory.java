package blocksmith.exec;

import blocksmith.app.block.BlockDefLibrary;
import blocksmith.app.block.BlockExecLibrary;
import blocksmith.domain.graph.Graph;

/**
 *
 * @author joost
 */
public class ExecutionSessionFactory {

    private final BlockDefLibrary defLibrary;
    private final BlockExecLibrary execLibrary;

    public ExecutionSessionFactory(BlockDefLibrary defLibrary, BlockExecLibrary execLibrary) {
        this.defLibrary = defLibrary;
        this.execLibrary = execLibrary;
    }

    public ExecutionSession create(Graph graph) {
        var sourceBlocks = new SourceBlockIndex(execLibrary);
        var engine = new ExecutionEngine(defLibrary, execLibrary, sourceBlocks);
        var state = new ExecutionState();
        var invalidator = new ExecutionInvalidator();
        var session = new ExecutionSession(engine, state, invalidator, sourceBlocks, graph);
        return session;
    }

}

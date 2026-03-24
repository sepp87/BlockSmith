package blocksmith.exec;

import blocksmith.app.block.BlockDefLibrary;
import blocksmith.app.block.BlockFuncLibrary;
import blocksmith.domain.graph.Graph;

/**
 *
 * @author joost
 */
public class ExecutionSessionFactory {
    
    private final BlockDefLibrary defLibrary;
    private final BlockFuncLibrary funcLibrary;
    
    public ExecutionSessionFactory(BlockDefLibrary defLibrary, BlockFuncLibrary funcLibrary) {
        this.defLibrary = defLibrary;
        this.funcLibrary = funcLibrary;
    }

    public ExecutionSession create(Graph graph) {
        var engine = new ExecutionEngine(defLibrary, funcLibrary);
        var state = new ExecutionState();
        var invalidator = new ExecutionInvalidator();
        var session = new ExecutionSession(engine, state, invalidator, graph);
        return session;
    }

}

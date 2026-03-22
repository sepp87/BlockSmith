package blocksmith.exec;

import blocksmith.app.block.BlockDefLibrary;
import blocksmith.app.block.BlockFuncLibrary;
import blocksmith.domain.graph.Graph;

/**
 *
 * @author joost
 */
public class ForgeSessionFactory {
    
    private final BlockDefLibrary defLibrary;
    private final BlockFuncLibrary funcLibrary;
    
    public ForgeSessionFactory(BlockDefLibrary defLibrary, BlockFuncLibrary funcLibrary) {
        this.defLibrary = defLibrary;
        this.funcLibrary = funcLibrary;
    }

    public ForgeSession create(Graph graph) {
        var engine = new FlowEngine(defLibrary, funcLibrary);
        var state = new ForgeState();
        var invalidator = new ForgeInvalidator();
        var session = new ForgeSession(engine, state, invalidator, graph);
        return session;
    }

}

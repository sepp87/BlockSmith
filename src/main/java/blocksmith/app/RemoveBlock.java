package blocksmith.app;

import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.Connection;
import blocksmith.domain.graph.Graph;
import blocksmith.domain.graph.GraphDomainLogic;
import blocksmith.domain.group.Group;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joost
 */
public class RemoveBlock {
    
    private final static Logger LOGGER = Logger.getLogger(RemoveBlock.class.getName());

    public Graph execute(Graph graph, BlockId id) {
        if (graph.block(id).isEmpty()) {
            LOGGER.log(Level.INFO, "Cannot remove block, because it does not exist: {0}", id);
            return graph;
        }
        return graph.withoutBlock(id);
    }

}

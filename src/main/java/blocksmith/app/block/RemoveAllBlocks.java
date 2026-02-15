package blocksmith.app.block;

import blocksmith.domain.block.BlockId;
import blocksmith.domain.graph.Graph;
import java.util.Collection;

/**
 *
 * @author joost
 */
public class RemoveAllBlocks {

    public Graph execute(Graph graph, Collection<BlockId> blocks) {
        return graph.withoutBlocks(blocks);
    }
    
}

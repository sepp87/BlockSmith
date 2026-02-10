package blocksmith.app;

import blocksmith.domain.block.BlockFactory;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.graph.Graph;

/**
 *
 * @author joostmeulenkamp
 */
public final class AddBlock {
    
    private final BlockFactory factory;
    
    public AddBlock(BlockFactory factory) {
        this.factory = factory;
    }
    
    public Graph execute(Graph graph, BlockId id, String type ) {
        var block = factory.create(id, type);
        return graph.withBlock(block);
    }
    
}

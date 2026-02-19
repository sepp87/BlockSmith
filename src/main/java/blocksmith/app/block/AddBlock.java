package blocksmith.app.block;

import blocksmith.domain.block.BlockFactory;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.block.BlockPosition;
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

    public Graph execute(Graph graph, BlockId id, String type, double x, double y) {
        var block = factory.create(id, type);
        block = block.withPosition(x, y);
        return graph.withBlock(block);
    }

}

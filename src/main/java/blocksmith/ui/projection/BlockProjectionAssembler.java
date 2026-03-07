package blocksmith.ui.projection;

import blocksmith.domain.block.Block;
import blocksmith.domain.block.BlockId;
import blocksmith.ui.BlockModelFactory;
import blocksmith.ui.graph.block.BlockModel;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author joost
 */
public class BlockProjectionAssembler {

    private final BlockModelFactory blockFactory;

    public BlockProjectionAssembler(BlockModelFactory blockFactory) {
        this.blockFactory = blockFactory;
    }

    public Map<BlockId, BlockModel> create(Collection<Block> blocks) {
        var result = new HashMap<BlockId, BlockModel>();
        for (var block : blocks) {
            var model = blockFactory.create(block.type(), block.id().toString());
            model.updateFrom(block);
            model.setActive(true);
            result.put(block.id(), model);
        }
        return Map.copyOf(result);
    }
}

package blocksmith.infra.xml;

import blocksmith.domain.block.Block;
import blocksmith.domain.block.BlockFactory;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.block.BlockLayout;
import blocksmith.xml.v2.BlockXml;
import blocksmith.xml.v2.BlocksXml;
import blocksmith.xml.v2.ObjectFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author joost
 */
public class BlockXmlMapper {

    private final ObjectFactory xmlFactory;
    private final BlockFactory blockFactory;

    public BlockXmlMapper(ObjectFactory xmlFactory, BlockFactory blockFactory) {
        this.xmlFactory = xmlFactory;
        this.blockFactory = blockFactory;
    }

    public List<Block> toDomain(List<BlockXml> blockXmls) {
        var result = new ArrayList<Block>();

        for (var blockXml : blockXmls) {

            var id = BlockId.from(blockXml.getId());
            var type = blockXml.getType();
            var block = blockFactory.create(id, type);
            var layout = layoutToDomain(blockXml);
            var updated = block.withLayout(layout);

            result.add(updated);

        }
        return result;
    }

    private BlockLayout layoutToDomain(BlockXml blockXml) {

        var label = blockXml.getLabel();
        var x = blockXml.getX();
        var y = blockXml.getY();
        var width = blockXml.getWidth();
        var height = blockXml.getHeight();

        return BlockLayout.create(label, x, y, width, height);
    }

    public BlocksXml toXml(Collection<Block> blocks) {
        var result = xmlFactory.createBlocksXml();

        for (var block : blocks) {
            var blockXml = layoutToXml(block);
            blockXml.setId(block.id().toString());
            blockXml.setType(block.type());
            result.getBlock().add(blockXml);
        }

        return result;
    }

    public BlockXml layoutToXml(Block block) {
        var blockXml = xmlFactory.createBlockXml();

        var layout = block.layout();
        blockXml.setLabel(layout.label());
        blockXml.setX(layout.x());
        blockXml.setY(layout.y());
        blockXml.setWidth(layout.width());
        blockXml.setHeight(layout.height());

        return blockXml;
    }

}

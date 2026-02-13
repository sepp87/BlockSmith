package blocksmith.infra.xml;

import blocksmith.domain.block.Block;
import blocksmith.domain.block.BlockFactory;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.block.EditorMetadata;
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
            var metadata = editorMetadataToDomain(blockXml);
            var updated = block.withEditorMetadata(metadata);

            result.add(updated);

        }
        return result;
    }

    private EditorMetadata editorMetadataToDomain(BlockXml blockXml) {

        var label = blockXml.getLabel();
        var x = blockXml.getX();
        var y = blockXml.getY();
        var width = blockXml.getWidth();
        var height = blockXml.getHeight();

        return new EditorMetadata(label, x, y, width, height);
    }

    public BlocksXml toXml(Collection<Block> blocks) {
        var result = xmlFactory.createBlocksXml();

        for (var block : blocks) {
            var blockXml = editorMetadataToXml(block);
            blockXml.setId(block.id().toString());
            blockXml.setType(block.type());
            result.getBlock().add(blockXml);
        }

        return result;
    }

    public BlockXml editorMetadataToXml(Block block) {
        var blockXml = xmlFactory.createBlockXml();
        
        if (block.editorMetadata().isPresent()) {
            var metadata = block.editorMetadata().get();
            blockXml.setLabel(metadata.label());
            blockXml.setX(metadata.x());
            blockXml.setY(metadata.y());
            blockXml.setWidth(metadata.width());
            blockXml.setHeight(metadata.height());
        }
        return blockXml;
    }

}

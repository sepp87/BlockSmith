package blocksmith.adapter.graph;

import blocksmith.domain.block.Block;
import blocksmith.domain.block.BlockId;
import blocksmith.xml.v2.ObjectFactory;
import blocksmith.xml.v2.ValueXml;
import blocksmith.xml.v2.ValuesXml;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joost
 */
public class ValueXmlMapper {

    private final static Logger LOGGER = Logger.getLogger(ValueXmlMapper.class.getName());

    private final ObjectFactory xmlFactory;

    public ValueXmlMapper(ObjectFactory xmlFactory) {
        this.xmlFactory = xmlFactory;
    }

    public Collection<Block> toDomain(Collection<Block> blocks, List<ValueXml> valueXmls) {
        var index = new HashMap<BlockId, Block>();
        blocks.stream().forEach(b -> index.put(b.id(), b));

        for (var valueXml : valueXmls) {
            var blockId = BlockId.from(valueXml.getBlock());
            var valueId = valueXml.getId();
            var value = valueXml.getValue();
            var block = index.get(blockId);
            if (block == null) {
                LOGGER.log(Level.WARNING, "Value skipped for missing block: {0} {1}", new Object[]{valueId, blockId.toString()});
                continue;
            }
            var updated = block.withParamValue(valueId, value);
            index.put(blockId, updated);

        }
        return index.values();
    }

    public ValuesXml toXml(Collection<Block> blocks) {
        var result = xmlFactory.createValuesXml();

        var values = new ArrayList<ValueXml>();
        blocks.forEach(b -> b.params().forEach(p -> {
            if (p.isActive()) {
                var valueXml = xmlFactory.createValueXml();
                valueXml.setBlock(b.id().toString());
                valueXml.setId(p.valueId());
                valueXml.setValue(p.value());
                values.add(valueXml);
            }
        }));
        result.getValue().addAll(values);

        return result;
    }

}

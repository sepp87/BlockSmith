package blocksmith.infra.xml;

import blocksmith.app.logging.GraphLogFmt;
import blocksmith.domain.block.Block;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.graph.ParamStatusResolver;
import blocksmith.domain.value.ParamInput;
import blocksmith.xml.v2.ObjectFactory;
import blocksmith.xml.v2.ValueXml;
import blocksmith.xml.v2.ValuesXml;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;

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
                LOGGER.log(Level.WARNING, "Value skipped for missing block: {0}.{1}", new Object[]{GraphLogFmt.block(blockId), valueId});
                continue;
            }
            var updated = block.withParamValue(valueId, value);
            
            var param = updated.param(valueId).orElse(null);
            if (param == null) {
                LOGGER.log(Level.WARNING, "Value skipped for missing param: {0}.{1}", new Object[]{GraphLogFmt.block(blockId), valueId});
                continue;
            }

            if (param.input() instanceof ParamInput.Range range) {
                var min = boundOfRange(valueXml, "min");
                var max = boundOfRange(valueXml, "max");
                var step = boundOfRange(valueXml, "step");
                updated = updated.withParamInput(valueId, range.withBounds(min, max, step));
            }

            index.put(blockId, updated);

        }
        return index.values();
    }

    private static double boundOfRange(ValueXml valueXml, String name) {
        var raw = valueXml.getOtherAttributes().get(QName.valueOf(name));
        return Double.valueOf(raw);
    }

    public ValuesXml toXml(Collection<Block> blocks) {
        var result = xmlFactory.createValuesXml();

        var values = new ArrayList<ValueXml>();
        blocks.forEach(b -> b.params().forEach(p -> {

            var valueXml = xmlFactory.createValueXml();
            valueXml.setBlock(b.id().toString());
            valueXml.setId(p.valueId());
            valueXml.setValue(p.value());
            values.add(valueXml);

            if (p.input() instanceof ParamInput.Range range) {
                valueXml.getOtherAttributes().put(QName.valueOf("min"), String.valueOf(range.min()));
                valueXml.getOtherAttributes().put(QName.valueOf("max"), String.valueOf(range.max()));
                valueXml.getOtherAttributes().put(QName.valueOf("step"), String.valueOf(range.step()));
            }

//            
        }));
        result.getValue().addAll(values);

        return result;
    }

}

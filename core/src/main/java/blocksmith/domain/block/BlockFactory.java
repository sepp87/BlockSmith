package blocksmith.domain.block;

import blocksmith.domain.value.Port;
import blocksmith.app.block.BlockDefLibrary;
import blocksmith.domain.value.Param;
import blocksmith.domain.value.PortDef;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author joost
 */
public class BlockFactory {

    private static final String DEFAULT_VALUE = null;
    private final BlockDefLibrary library;

    public BlockFactory(BlockDefLibrary library) {
        this.library = library;
    }

    public Block create(BlockId id, String type) {
        var def = library.resolve(type).orElse(null);
        if (def == null) {
            return UnknownBlock.create(id, type);
        }
        var blockType = def.type();
        var ports = createPorts(def);
        var params = createParams(def);

        if (def.hasAggregatedInput()) {
            return new ArrayBlock(id, type, params, ports, null);

        } else {
            return new Block(id, blockType, params, ports, null);
        }
    }

    private List<Port> createPorts(BlockDef def) {
        var inputs = createPortsFrom(def.inputs());
        var outputs = createPortsFrom(def.outputs());
        return Stream.concat(inputs.stream(), outputs.stream()).toList();
    }

    private List<Port> createPortsFrom(List<PortDef> portDefs) {
        var result = new ArrayList<Port>();
        for (var portDef : portDefs) {
            var isElement = portDef.isAggregatedValue();
            var valueId = !isElement ? portDef.valueId() : portDef.valueId() + "#0";
            var port = new Port(portDef.direction(), valueId, portDef.argIndex(), portDef.valueType(), isElement);
            result.add(port);
        }
        return result;
    }

    private List<Param> createParams(BlockDef def) {
        var result = new ArrayList<Param>();

        for (var paramDef : def.params()) {
            var param = new Param(paramDef.valueId(), paramDef.argIndex(), DEFAULT_VALUE, paramDef.input());
            // TODO replace DEFAULT_VALUE with paramDef.defaultValue()
            result.add(param);
        }

        return result;
    }

}

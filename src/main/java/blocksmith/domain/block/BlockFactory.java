package blocksmith.domain.block;

import blocksmith.domain.value.Port;
import blocksmith.app.BlockDefLibrary;
import blocksmith.domain.value.Param;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author joost
 */
public class BlockFactory {

    private final BlockDefLibrary library;

    public BlockFactory(BlockDefLibrary library) {
        this.library = library;
    }

    public Block create(BlockId id, String type) {
        var oDef = library.resolve(type);
        if (oDef.isEmpty()) {
            throw new IllegalArgumentException("Creation aborted, block type unknown: " + type);
        }
        var def = oDef.get();
        var blockType = def.metadata().type();
        var ports = createPorts(def);
        var params = createParams(def);
        var block = new Block(id, blockType, ports, params, null);

        return block;
    }

    private List<Port> createPorts(BlockDef def) {
        var result = new ArrayList<Port>();

        for (var portDef : def.inputs()) {
            var port = new Port(Port.Direction.INPUT, portDef.valueId(),  null, portDef.valueType());
            result.add(port);
        }

        return result;
    }

    private List<Param> createParams(BlockDef def) {
        var result = new ArrayList<Param>();

        for (var paramDef : def.params()) {
            var param = new Param(paramDef.valueId(), null, true);
            result.add(param);
        }

        return result;
    }

}

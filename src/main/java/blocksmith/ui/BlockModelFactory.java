package blocksmith.ui;

import blocksmith.app.BlockDefLibrary;
import blocksmith.app.BlockFuncLibrary;
import btscore.graph.block.BlockModel;

/**
 *
 * @author joostmeulenkamp
 */
public class BlockModelFactory {

    private final BlockDefLibrary defLibrary;
    private final BlockFuncLibrary funcLibrary;

    public BlockModelFactory(BlockDefLibrary defLibrary, BlockFuncLibrary funcLibrary) {
        this.defLibrary = defLibrary;
        this.funcLibrary = funcLibrary;
    }

    public BlockModel create(String type) {
        var def = defLibrary.findByType(type).get();
        var func = funcLibrary.findByType(type).get();

        var block = new MethodBlockNew(def, func);

        for (var input : def.inputs()) {
            block.addInputPort(input.name(), input.dataType());
        }

        for (var output : def.outputs()) {
            block.addOutputPort(output.name(), output.dataType());
        }
        
        block.isListOperator = def.isListOperator();
        block.isListWithUnknownReturnType = def.outputs().getFirst().dataTypeIsGeneric();

        return block;
    }

}

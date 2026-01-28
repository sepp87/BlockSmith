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
        
        return null;
    }
    
}

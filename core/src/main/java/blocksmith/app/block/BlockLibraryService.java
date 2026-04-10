
package blocksmith.app.block;

import blocksmith.app.outbound.BlockDefLoader;
import blocksmith.app.outbound.BlockFuncLoader;

/**
 *
 * @author joost
 */
public final class BlockLibraryService {

    private final BlockDefLoader defLoader;
    private final BlockFuncLoader funcLoader;
    
    private BlockDefLibrary defs;
    private BlockFuncLibrary funcs;
    
    public BlockLibraryService(BlockDefLoader defLoader, BlockFuncLoader funcLoader) {
        this.defLoader = defLoader;
        this.funcLoader = funcLoader;
        reload();
    }
    
    public void reload() {
        defs = new BlockDefLibrary(defLoader.load());
        funcs = new BlockFuncLibrary(funcLoader.load());
    }
    
    public BlockDefLibrary defs() {
        return defs;
    }
    
    public BlockFuncLibrary funcs() {
        return funcs;
    }
    
}

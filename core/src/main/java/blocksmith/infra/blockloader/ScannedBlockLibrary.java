
package blocksmith.infra.blockloader;

import blocksmith.app.block.BlockDefLibrary;
import blocksmith.app.block.BlockFuncLibrary;
import blocksmith.app.outbound.BlockDefLoader;
import blocksmith.app.outbound.BlockFuncLoader;
import blocksmith.app.outbound.BlockLibrary;

/**
 *
 * @author joost
 */
public final class ScannedBlockLibrary implements BlockLibrary {

    private final ClassScanner classScanner;
    private final MethodBlockScanner methodScanner;
    private final BlockDefLoader defLoader;
    private final BlockFuncLoader funcLoader;
    
    private BlockDefLibrary defs;
    private BlockFuncLibrary funcs;
    
    public ScannedBlockLibrary(
            ClassScanner classScanner,
            MethodBlockScanner methodScanner,
            BlockDefLoader defLoader, 
            BlockFuncLoader funcLoader) {
        
        this.classScanner = classScanner;
        this.methodScanner = methodScanner;
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

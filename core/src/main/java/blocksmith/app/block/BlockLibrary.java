package blocksmith.app.block;

import blocksmith.app.outbound.BlockDefLoader;
import blocksmith.app.outbound.BlockExecLoader;
import blocksmith.app.outbound.BlockScanner;

/**
 *
 * @author joost
 */
public class BlockLibrary {

    private final BlockScanner blockScanner;
    private final BlockDefLoader defLoader;
    private final BlockExecLoader execLoader;

    private BlockDefLibrary defs;
    private BlockExecLibrary execs;

    public BlockLibrary(
            BlockScanner blockScanner,
            BlockDefLoader defLoader,
            BlockExecLoader execLoader) {

        this.blockScanner = blockScanner;
        this.defLoader = defLoader;
        this.execLoader = execLoader;
        reload();
    }

    public void reload() {
        blockScanner.rescan();
        defs = new BlockDefLibrary(defLoader.load());
        execs = new BlockExecLibrary(execLoader.load());
    }

    public BlockDefLibrary defs() {
        return defs;
    }

    public BlockExecLibrary execs() {
        return execs;
    }

}

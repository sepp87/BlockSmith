package blocksmith.app.outbound;

import blocksmith.app.block.BlockDefLibrary;
import blocksmith.app.block.BlockFuncLibrary;

/**
 *
 * @author joost
 */
public interface BlockLibrary {

    void reload();

    BlockDefLibrary defs();

    BlockFuncLibrary funcs();
}

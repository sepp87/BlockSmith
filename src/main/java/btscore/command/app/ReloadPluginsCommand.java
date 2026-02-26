package btscore.command.app;

import btscore.Launcher;
import btscore.command.AppCommand;
import btscore.graph.block.BlockLibraryLoader;

/**
 *
 * @author joostmeulenkamp
 */
public class ReloadPluginsCommand implements AppCommand {

    public ReloadPluginsCommand() {
    }

    @Override
    public boolean execute() {

        if (Launcher.BLOCK_DEF_LOADER) {
            return true;
        }
        BlockLibraryLoader.reloadExternalBlocks();
        return true;

    }

}

package blocksmith.ui.command.app;

import blocksmith.Launcher;
import blocksmith.ui.command.AppCommand;
import blocksmith.ui.graph.block.BlockLibraryLoader;

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

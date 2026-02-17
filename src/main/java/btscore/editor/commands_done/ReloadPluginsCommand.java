package btscore.editor.commands_done;

import btscore.editor.context.Command;
import btscore.graph.block.BlockLibraryLoader;
import btscore.workspace.WorkspaceContext;

/**
 *
 * @author joostmeulenkamp
 */
public class ReloadPluginsCommand implements Command {

    public ReloadPluginsCommand() {
    }

    @Override
    public boolean execute(WorkspaceContext context) {
        BlockLibraryLoader.reloadExternalBlocks();
        return true;

    }

}

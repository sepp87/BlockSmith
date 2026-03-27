package blocksmith.app.block.command;

import blocksmith.domain.block.BlockId;
import blocksmith.app.workspace.WorkspaceCommand;
import blocksmith.ui.workspace.WorkspaceController;
import blocksmith.app.workspace.WorkspaceSession;

/**
 *
 * @author JoostMeulenkamp
 */
public class CopyBlocksCommand implements WorkspaceCommand {
    
    private final WorkspaceSession workspace;
    
    public CopyBlocksCommand(WorkspaceSession workspace) {
        this.workspace = workspace;
    }
    
    @Override
    public boolean execute() {
        var blocks = workspace.selection().selected();
        workspace.graphEditor().copyBlocks(blocks);
        return true;
    }
    
}

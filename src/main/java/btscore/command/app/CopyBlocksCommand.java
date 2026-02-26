package btscore.command.app;

import blocksmith.domain.block.BlockId;
import btscore.command.WorkspaceCommand;
import btscore.workspace.WorkspaceController;

/**
 *
 * @author JoostMeulenkamp
 */
public class CopyBlocksCommand implements WorkspaceCommand {
    
    private final WorkspaceController workspaceController;
    
    public CopyBlocksCommand(WorkspaceController workspaceController) {
        this.workspaceController = workspaceController;
    }
    
    @Override
    public boolean execute() {
        var blocks = workspaceController.getSelectedBlockControllers().stream().map(c -> BlockId.from(c.getModel().getId())).toList();
        workspaceController.getModel().graphEditor().copyBlocks(blocks);
        return true;
    }
    
}

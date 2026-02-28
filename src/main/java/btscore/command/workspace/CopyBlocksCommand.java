package btscore.command.workspace;

import blocksmith.domain.block.BlockId;
import btscore.command.WorkspaceCommand;
import btscore.workspace.WorkspaceController;
import btscore.workspace.WorkspaceModel;

/**
 *
 * @author JoostMeulenkamp
 */
public class CopyBlocksCommand implements WorkspaceCommand {
    
    private final WorkspaceModel workspace;
    
    public CopyBlocksCommand(WorkspaceModel workspace) {
        this.workspace = workspace;
    }
    
    @Override
    public boolean execute() {
        var blocks = workspace.selectionModel().selected();
        workspace.graphEditor().copyBlocks(blocks);
        return true;
    }
    
}

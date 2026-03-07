package blocksmith.ui.command.workspace;

import blocksmith.domain.block.BlockId;
import blocksmith.ui.command.WorkspaceCommand;
import blocksmith.ui.workspace.WorkspaceController;
import blocksmith.ui.workspace.WorkspaceSession;

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
        var blocks = workspace.selectionModel().selected();
        workspace.graphEditor().copyBlocks(blocks);
        return true;
    }
    
}

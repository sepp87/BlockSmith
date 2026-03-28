package blocksmith.app.block.command;

import blocksmith.app.command.WorkspaceCommand;
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

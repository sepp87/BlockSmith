package blocksmith.app.block.command;

import blocksmith.app.command.WorkspaceCommand;
import blocksmith.ui.workspace.WorkspaceController;
import blocksmith.app.workspace.WorkspaceSession;

/**
 *
 * @author Joost
 */
public class SelectAllBlocksCommand implements WorkspaceCommand {

    private final WorkspaceSession workspaceController;

    public SelectAllBlocksCommand(WorkspaceSession workspaceController) {
        this.workspaceController = workspaceController;
    }

    @Override
    public boolean execute() {
        workspaceController.selection().selectAll();
        return true;
    }


}

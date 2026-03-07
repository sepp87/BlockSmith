package blocksmith.ui.command.workspace;

import blocksmith.ui.command.WorkspaceCommand;
import blocksmith.ui.workspace.WorkspaceController;
import blocksmith.ui.workspace.WorkspaceSession;

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
        workspaceController.selectionModel().selectAll();
        return true;
    }


}

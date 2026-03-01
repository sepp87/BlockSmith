package btscore.command.workspace;

import btscore.command.WorkspaceCommand;
import btscore.workspace.WorkspaceController;
import btscore.workspace.WorkspaceModel;

/**
 *
 * @author Joost
 */
public class SelectAllBlocksCommand implements WorkspaceCommand {

    private final WorkspaceModel workspaceController;

    public SelectAllBlocksCommand(WorkspaceModel workspaceController) {
        this.workspaceController = workspaceController;
    }

    @Override
    public boolean execute() {
        workspaceController.selectionModel().selectAll();
        return true;
    }


}

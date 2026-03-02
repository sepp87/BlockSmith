package btscore.command.workspace;

import btscore.command.WorkspaceCommand;
import btscore.workspace.WorkspaceController;
import btscore.workspace.WorkspaceSession;

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

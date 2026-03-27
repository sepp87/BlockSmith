package blocksmith.app.group.command;

import blocksmith.app.workspace.WorkspaceCommand;
import blocksmith.app.workspace.WorkspaceSession;

/**
 *
 * @author Joost
 */
public class AddGroupCommand implements WorkspaceCommand {

    private final WorkspaceSession workspaceModel;

    public AddGroupCommand( WorkspaceSession workspaceModel) {
        this.workspaceModel = workspaceModel;
    }

    @Override
    public boolean execute() {

        boolean notGroupable = !workspaceModel.isSelectionGroupable();
        // do not execute if selected blocks is less than two
        if (notGroupable) {
            return false;
        }

        var ids = workspaceModel.selection().selected();
        workspaceModel.graphEditor().addGroup(null, ids);

        return true;
    }

}

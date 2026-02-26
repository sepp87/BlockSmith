package btscore.command.app;

import btscore.command.AppCommand;
import btscore.workspace.WorkspaceModel;

/**
 *
 * @author Joost
 */
public class NewFileCommand implements AppCommand {

    private final WorkspaceModel workspaceModel;

    public NewFileCommand(WorkspaceModel workspaceModel) {
        this.workspaceModel = workspaceModel;
    }

    @Override
    public boolean execute() {
        workspaceModel.reset();
        // Session Manager > create new Workspace Session
        return true;

    }

}

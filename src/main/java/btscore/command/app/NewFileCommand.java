package btscore.command.app;

import btscore.command.AppCommand;
import btscore.workspace.WorkspaceSession;

/**
 *
 * @author Joost
 */
public class NewFileCommand implements AppCommand {

    private final WorkspaceSession workspaceModel;

    public NewFileCommand(WorkspaceSession workspaceModel) {
        this.workspaceModel = workspaceModel;
    }

    @Override
    public boolean execute() {
        workspaceModel.reset();
        // Session Manager > create new Workspace Session
        return true;

    }

}

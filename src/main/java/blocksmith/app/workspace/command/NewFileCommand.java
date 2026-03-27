package blocksmith.app.workspace.command;

import blocksmith.app.workspace.WorkspaceLifecycle;
import blocksmith.app.command.AppCommand;
import blocksmith.app.command.AppCommand;

/**
 *
 * @author Joost
 */
public class NewFileCommand implements AppCommand {

    private final WorkspaceLifecycle workspaces;

    public NewFileCommand(WorkspaceLifecycle workspaces) {
        this.workspaces = workspaces;
    }

    @Override
    public boolean execute() {
        workspaces.newDocument();
        return true;

    }

}

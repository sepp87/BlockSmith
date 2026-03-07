package blocksmith.ui.command.app;

import blocksmith.app.workspace.WorkspaceLifecycle;
import blocksmith.ui.command.AppCommand;

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

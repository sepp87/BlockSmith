package blocksmith.ui.editor.navigation.command;

import blocksmith.app.command.WorkspaceCommand;
import blocksmith.ui.workspace.WorkspaceController;

/**
 * TODO not implemented
 *
 * @author Joost
 */
public class PanToFitCommand implements WorkspaceCommand {

    private final WorkspaceController workspace;

    public PanToFitCommand(WorkspaceController workspace) {
        this.workspace = workspace;
    }

    @Override
    public boolean execute() {
        return true;
    }

}

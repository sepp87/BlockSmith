package btscore.command.workspace;

import btscore.command.WorkspaceCommand;
import btscore.workspace.WorkspaceController;

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

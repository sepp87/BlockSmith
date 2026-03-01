package btscore.command.workspace;

import btscore.command.Command;
import btscore.workspace.WorkspaceContext;
import btscore.workspace.WorkspaceController;
import btscore.workspace.WorkspaceModel;

/**
 *
 * @author Joost
 */
public class DeselectAllBlocksCommand implements Command {

    private final WorkspaceModel workspace;

    public DeselectAllBlocksCommand(WorkspaceModel workspace) {
        this.workspace = workspace;
    }

    @Override
    public boolean execute() {
        workspace.selectionModel().deselectAll();
        return true;
    }

}

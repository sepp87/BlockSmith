package blocksmith.ui.command.workspace;

import blocksmith.ui.command.Command;
import blocksmith.ui.workspace.FxWorkspaceHandle;
import blocksmith.ui.workspace.WorkspaceController;
import blocksmith.ui.workspace.WorkspaceSession;

/**
 *
 * @author Joost
 */
public class DeselectAllBlocksCommand implements Command {

    private final WorkspaceSession workspace;

    public DeselectAllBlocksCommand(WorkspaceSession workspace) {
        this.workspace = workspace;
    }

    @Override
    public boolean execute() {
        workspace.selectionModel().deselectAll();
        return true;
    }

}

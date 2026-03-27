package blocksmith.app.block.command;

import blocksmith.app.workspace.Command;
import blocksmith.ui.workspace.WorkspaceFxHandle;
import blocksmith.ui.workspace.WorkspaceController;
import blocksmith.app.workspace.WorkspaceSession;

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
        workspace.selection().deselectAll();
        return true;
    }

}

package blocksmith.app.block.command;

import blocksmith.app.command.WorkspaceCommand;
import blocksmith.app.workspace.WorkspaceSession;

/**
 *
 * @author Joost
 */
public class DeselectAllBlocksCommand implements WorkspaceCommand {

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

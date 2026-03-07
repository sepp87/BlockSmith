package blocksmith.ui.command.workspace;

import blocksmith.ui.workspace.WorkspaceController;
import blocksmith.ui.workspace.WorkspaceSession;
import blocksmith.ui.command.WorkspaceCommand;

/**
 *
 * @author Joost
 */
public class RemoveBlocksCommand implements WorkspaceCommand {

    private final WorkspaceSession session;

//    private final WorkspaceController workspaceController;

    public RemoveBlocksCommand( WorkspaceSession session) {
//        this.workspaceController = workspaceController;
        this.session = session;
    }

    @Override
    public boolean execute() {

        var ids = session.selectionModel().selected();
        session.graphEditor().removeAllBlocks(ids);
        session.selectionModel().deselectAll();

        return true;

    }

}

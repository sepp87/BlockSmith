package btscore.command.workspace;

import btscore.workspace.WorkspaceController;
import btscore.workspace.WorkspaceModel;
import btscore.command.WorkspaceCommand;

/**
 *
 * @author Joost
 */
public class RemoveBlocksCommand implements WorkspaceCommand {

    private final WorkspaceModel session;

//    private final WorkspaceController workspaceController;

    public RemoveBlocksCommand( WorkspaceModel session) {
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

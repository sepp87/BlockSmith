package btscore.command.workspace;

import blocksmith.domain.block.BlockId;
import btscore.workspace.WorkspaceController;
import btscore.workspace.WorkspaceModel;
import btscore.command.WorkspaceCommand;

/**
 *
 * @author Joost
 */
public class RemoveBlocksCommand implements WorkspaceCommand {

    private final WorkspaceModel session;

    private final WorkspaceController workspaceController;

    public RemoveBlocksCommand(WorkspaceController workspaceController, WorkspaceModel session) {
        this.workspaceController = workspaceController;
        this.session = session;
    }

    @Override
    public boolean execute() {

        var ids = workspaceController.getSelectedBlockControllers().stream().map(c -> BlockId.from(c.getModel().getId())).toList();
        session.graphEditor().removeAllBlocks(ids);
        workspaceController.deselectAllBlocks();

        return true;

    }

}

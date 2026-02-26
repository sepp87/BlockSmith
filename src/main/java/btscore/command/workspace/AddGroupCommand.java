package btscore.command.workspace;

import blocksmith.domain.block.BlockId;
import btscore.command.WorkspaceCommand;
import java.util.Collection;
import btscore.graph.block.BlockController;
import btscore.graph.group.BlockGroupModel;
import btscore.workspace.WorkspaceController;
import btscore.workspace.WorkspaceModel;

/**
 *
 * @author Joost
 */
public class AddGroupCommand implements WorkspaceCommand {

    private final WorkspaceController workspaceController;
    private final WorkspaceModel workspaceModel;
    private final Collection<BlockController> blocks;
    private BlockGroupModel group;

    public AddGroupCommand(WorkspaceController workspaceController, WorkspaceModel workspaceModel) {
        this.workspaceController = workspaceController;
        this.workspaceModel = workspaceModel;
        this.blocks = workspaceController.getSelectedBlockControllers();
    }

    @Override
    public boolean execute() {

        boolean notGroupable = !workspaceModel.isSelectionGroupable();
        // do not execute if selected blocks is less than two
        if (notGroupable || blocks.size() < 2) {
            return false;
        }

        var ids = workspaceController.getSelectedBlockControllers().stream().map(c -> BlockId.from(c.getModel().getId())).toList();
        workspaceModel.graphEditor().addGroup(null, ids);

        return true;
    }

}

package btscore.editor.commands_done;

import blocksmith.domain.block.BlockId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import btscore.graph.block.BlockController;
import btscore.graph.group.BlockGroupModel;
import btscore.graph.block.BlockModel;
import btscore.workspace.WorkspaceController;
import btscore.workspace.WorkspaceModel;
import btscore.editor.context.UndoableCommand;
import btscore.workspace.WorkspaceContext;

/**
 *
 * @author Joost
 */
public class AddGroupCommand implements UndoableCommand {

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
    public boolean execute(WorkspaceContext context) {
        var ids = workspaceController.getSelectedBlockControllers().stream().map(c -> BlockId.from(c.getModel().getId())).toList();
        workspaceModel.addGroup(null, ids);

        // OLD STUFF
        boolean notGroupable = !workspaceController.areSelectedBlocksGroupable();

        System.out.println("GroupBlocksCommand.execute()");
        // do not execute if selected blocks is less than two
        if (notGroupable || blocks.size() < 2) {
            return false;
        }
        List<BlockModel> blockModels = new ArrayList<>();
        for (BlockController blockController : blocks) {
            blockModels.add(blockController.getModel());
        }

        if (group == null) { // Create the group, because it was not yet created
            group = new BlockGroupModel(workspaceModel.getBlockGroupIndex());

        } else { // Revive the group, because it was removed through undo
            group.revive();
        }
        group.setBlocks(blockModels);
        workspaceModel.addBlockGroupModel(group);
        return true;
    }

    @Override
    public void undo() {
        if (group != null) { // Selected number of blocks was more than one, TODO this command should NOT have been recorded
            workspaceModel.removeBlockGroupModel(group);
        }
    }
}

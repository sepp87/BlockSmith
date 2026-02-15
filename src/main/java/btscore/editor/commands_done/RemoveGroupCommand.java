package btscore.editor.commands_done;

import blocksmith.ui.WorkspaceSession;
import java.util.ArrayList;
import java.util.List;
import btscore.graph.block.BlockModel;
import btscore.graph.group.BlockGroupModel;
import btscore.workspace.WorkspaceModel;
import btscore.editor.context.UndoableCommand;
import btscore.workspace.WorkspaceContext;

/**
 *
 * @author Joost
 */
public class RemoveGroupCommand implements UndoableCommand {
    private final WorkspaceSession session;

    private final WorkspaceModel workspaceModel;
    private final BlockGroupModel group;
    private final List<BlockModel> blocks;

    public RemoveGroupCommand(WorkspaceModel workspaceModel, BlockGroupModel group, WorkspaceSession session) {
        this.workspaceModel = workspaceModel;
        this.group = group;
        this.blocks = new ArrayList<>(group.getBlocks());
        this.session = session;
    }

    @Override
    public boolean execute(WorkspaceContext context) {
        workspaceModel.removeBlockGroupModel(group);
        return true;

    }

    @Override
    public void undo() {
        group.revive();
        group.setBlocks(blocks);
        workspaceModel.addBlockGroupModel(group);
    }
}

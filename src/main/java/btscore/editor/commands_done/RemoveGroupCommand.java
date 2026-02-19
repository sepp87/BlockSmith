package btscore.editor.commands_done;

import blocksmith.domain.block.BlockId;
import btscore.Launcher;
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

    private final WorkspaceModel workspaceModel;
    private final BlockGroupModel group;
    private final List<BlockModel> blocks;

    public RemoveGroupCommand(WorkspaceModel workspaceModel, BlockGroupModel group) {
        this.workspaceModel = workspaceModel;
        this.group = group;
        this.blocks = new ArrayList<>(group.getBlocks());
    }

    @Override
    public boolean execute(WorkspaceContext context) {
        var ids = blocks.stream().map(b -> BlockId.from(b.getId())).toList();
        workspaceModel.graphEditor().removeGroup(group.nameProperty().get(), ids);
        
        
        if(Launcher.DOMAIN_GRAPH) {
            return true;
        }
        
        // OLD STUFF
        workspaceModel.removeBlockGroupModel(group);
        return true;

    }

    @Override
    public void undo() {
        
        
        if(Launcher.DOMAIN_GRAPH) {
            return;
        }
        
        group.revive();
        group.setBlocks(blocks);
        workspaceModel.addBlockGroupModel(group);
    }
}

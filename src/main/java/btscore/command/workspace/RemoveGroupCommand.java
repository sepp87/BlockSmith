package btscore.command.workspace;

import blocksmith.domain.group.GroupId;
import java.util.ArrayList;
import java.util.List;
import btscore.graph.block.BlockModel;
import btscore.graph.group.BlockGroupModel;
import btscore.workspace.WorkspaceModel;
import btscore.command.WorkspaceCommand;

/**
 *
 * @author Joost
 */
public class RemoveGroupCommand implements WorkspaceCommand {

    private final WorkspaceModel workspaceModel;
    private final BlockGroupModel group;
    private final List<BlockModel> blocks;

    public RemoveGroupCommand(WorkspaceModel workspaceModel, BlockGroupModel group) {
        this.workspaceModel = workspaceModel;
        this.group = group;
        this.blocks = new ArrayList<>(group.getBlocks());
    }

    @Override
    public boolean execute() {
        var id = GroupId.from(group.getId());
        workspaceModel.graphEditor().removeGroup(id);
 
        return true;

    }

}

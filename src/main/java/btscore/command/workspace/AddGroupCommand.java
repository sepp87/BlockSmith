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

    private final WorkspaceModel workspaceModel;

    public AddGroupCommand( WorkspaceModel workspaceModel) {
        this.workspaceModel = workspaceModel;
    }

    @Override
    public boolean execute() {

        boolean notGroupable = !workspaceModel.isSelectionGroupable();
        // do not execute if selected blocks is less than two
        if (notGroupable) {
            return false;
        }

        var ids = workspaceModel.selectionModel().selected();
        workspaceModel.graphEditor().addGroup(null, ids);

        return true;
    }

}

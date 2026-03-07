package blocksmith.ui.command.workspace;

import blocksmith.domain.block.BlockId;
import blocksmith.ui.command.WorkspaceCommand;
import java.util.Collection;
import blocksmith.ui.graph.block.BlockController;
import blocksmith.ui.graph.group.BlockGroupModel;
import blocksmith.ui.workspace.WorkspaceController;
import blocksmith.ui.workspace.WorkspaceSession;

/**
 *
 * @author Joost
 */
public class AddGroupCommand implements WorkspaceCommand {

    private final WorkspaceSession workspaceModel;

    public AddGroupCommand( WorkspaceSession workspaceModel) {
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

package btscore.command.workspace;

import blocksmith.domain.block.BlockId;
import btscore.command.WorkspaceCommand;
import btscore.workspace.WorkspaceModel;
import java.util.List;

/**
 *
 * @author Joost
 */
public class UpdateSelectionCommand implements WorkspaceCommand {

    private final WorkspaceModel workspace;
    private final BlockId block;
    private final boolean isModifierDown;

    public UpdateSelectionCommand(WorkspaceModel workspace, BlockId block, boolean isModifierDown) {
        this.workspace = workspace;
        this.block = block;
        this.isModifierDown = isModifierDown;
    }

    @Override
    public boolean execute() {
        if (isModifierDown) {
            workspace.selectionModel().toggle(block);
        } else {
            workspace.selectionModel().select(List.of(block));
        }
        return true;
    }

}

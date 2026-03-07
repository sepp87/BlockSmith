package blocksmith.ui.command.workspace;

import blocksmith.domain.block.BlockId;
import blocksmith.ui.command.WorkspaceCommand;
import blocksmith.ui.workspace.WorkspaceSession;
import java.util.List;

/**
 *
 * @author Joost
 */
public class UpdateSelectionCommand implements WorkspaceCommand {

    private final WorkspaceSession workspace;
    private final BlockId block;
    private final boolean isModifierDown;

    public UpdateSelectionCommand(WorkspaceSession workspace, BlockId block, boolean isModifierDown) {
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

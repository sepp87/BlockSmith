package blocksmith.app.block.command;

import blocksmith.domain.block.BlockId;
import blocksmith.app.command.WorkspaceCommand;
import blocksmith.app.workspace.WorkspaceSession;
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
            workspace.selection().toggle(block);
        } else {
            workspace.selection().select(List.of(block));
        }
        return true;
    }

}

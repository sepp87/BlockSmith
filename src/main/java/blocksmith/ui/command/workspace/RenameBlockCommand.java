package blocksmith.ui.command.workspace;

import blocksmith.domain.block.BlockId;
import blocksmith.ui.command.WorkspaceCommand;
import blocksmith.ui.workspace.WorkspaceSession;

/**
 *
 * @author JoostMeulenkamp
 */
public class RenameBlockCommand implements WorkspaceCommand {

    private final WorkspaceSession workspace;
    private final BlockId block;
    private final String label;

    public RenameBlockCommand(WorkspaceSession workspace, BlockId block, String label) {
        this.workspace = workspace;
        this.block = block;
        this.label = label;
    }

    @Override
    public boolean execute() {
        workspace.graphEditor().renameBlock(block, label);
        return true;
    }
}

package blocksmith.app.block.command;

import blocksmith.domain.block.BlockId;
import blocksmith.app.command.WorkspaceCommand;
import blocksmith.app.workspace.WorkspaceSession;

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

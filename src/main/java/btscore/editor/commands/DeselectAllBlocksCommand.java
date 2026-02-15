package btscore.editor.commands;

import btscore.editor.context.Command;
import btscore.workspace.WorkspaceContext;
import btscore.workspace.WorkspaceController;

/**
 *
 * @author Joost
 */
public class DeselectAllBlocksCommand implements Command {

    private final WorkspaceController workspace;

    public DeselectAllBlocksCommand(WorkspaceController workspace) {
        this.workspace = workspace;
    }

    @Override
    public boolean execute(WorkspaceContext context) {
        workspace.deselectAllBlocks();
        return true;
    }

}

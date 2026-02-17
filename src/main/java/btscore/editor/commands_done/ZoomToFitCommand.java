package btscore.editor.commands_done;

import btscore.editor.context.Command;
import btscore.workspace.WorkspaceContext;
import btscore.workspace.WorkspaceController;

/**
 *
 * @author JoostMeulenkamp
 */
public class ZoomToFitCommand implements Command {

    private final WorkspaceController workspace;

    public ZoomToFitCommand(WorkspaceController workspace) {
        this.workspace = workspace;
    }

    @Override
    public boolean execute(WorkspaceContext context) {
        workspace.zoomToFit();
        return true;
    }



}

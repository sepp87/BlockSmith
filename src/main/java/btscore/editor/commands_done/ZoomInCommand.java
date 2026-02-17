package btscore.editor.commands_done;

import btscore.editor.context.Command;
import btscore.workspace.WorkspaceContext;
import btscore.workspace.WorkspaceController;

/**
 *
 * @author JoostMeulenkamp
 */
public class ZoomInCommand implements Command {

    private final WorkspaceController workspace;

    public ZoomInCommand(WorkspaceController workspace) {
        this.workspace = workspace;
    }

    @Override
    public boolean execute(WorkspaceContext context) {
        workspace.zoomIn();
        return true;
    }



}

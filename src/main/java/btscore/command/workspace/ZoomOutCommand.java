package btscore.command.workspace;

import btscore.command.WorkspaceCommand;
import btscore.workspace.WorkspaceController;
import btscore.workspace.ZoomService;

/**
 *
 * @author JoostMeulenkamp
 */
public class ZoomOutCommand implements WorkspaceCommand {

    private final ZoomService zoomService;

    public ZoomOutCommand(ZoomService zoomService) {
        this.zoomService = zoomService;
    }

    @Override
    public boolean execute() {
        zoomService.zoomOut();
        return true;
    }


}

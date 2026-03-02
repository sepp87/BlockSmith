package btscore.command.workspace;

import btscore.command.WorkspaceCommand;
import btscore.workspace.WorkspaceController;
import btscore.workspace.ZoomService;

/**
 *
 * @author JoostMeulenkamp
 */
public class ZoomInCommand implements WorkspaceCommand {

    private final ZoomService zoomService;

    public ZoomInCommand(ZoomService zoomService) {
        this.zoomService = zoomService;
    }

    @Override
    public boolean execute() {
        zoomService.zoomIn();
        return true;
    }



}

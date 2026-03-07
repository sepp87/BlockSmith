package blocksmith.ui.command.workspace;

import blocksmith.ui.command.WorkspaceCommand;
import blocksmith.ui.workspace.WorkspaceController;
import blocksmith.ui.workspace.ZoomService;

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

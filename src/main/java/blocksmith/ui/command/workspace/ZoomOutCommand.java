package blocksmith.ui.command.workspace;

import blocksmith.app.workspace.WorkspaceCommand;
import blocksmith.ui.workspace.WorkspaceController;
import blocksmith.ui.workspace.ZoomService;

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

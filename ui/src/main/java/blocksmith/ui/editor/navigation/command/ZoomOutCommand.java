package blocksmith.ui.editor.navigation.command;

import blocksmith.app.command.WorkspaceCommand;
import blocksmith.ui.workspace.WorkspaceController;
import blocksmith.ui.editor.navigation.ZoomService;

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

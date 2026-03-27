package blocksmith.ui.editor.navigation.command;

import blocksmith.app.command.WorkspaceCommand;
import blocksmith.ui.workspace.WorkspaceController;
import blocksmith.ui.editor.navigation.ZoomService;

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

package blocksmith.ui.editor.navigation.command;

import blocksmith.app.command.WorkspaceCommand;
import blocksmith.ui.editor.navigation.ZoomService;

/**
 *
 * @author JoostMeulenkamp
 */
public class ZoomToFitCommand implements WorkspaceCommand {

    private final ZoomService zoomService;

    public ZoomToFitCommand(ZoomService zoomService) {
        this.zoomService = zoomService;
    }

    @Override
    public boolean execute() {
        zoomService.zoomToFit();
        return true;
    }



}

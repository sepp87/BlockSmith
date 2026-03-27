package blocksmith.ui.command.workspace;

import blocksmith.app.workspace.Command;
import blocksmith.ui.workspace.ZoomService;

/**
 *
 * @author JoostMeulenkamp
 */
public class ZoomToFitCommand implements Command {

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

package btscore.command.workspace;

import btscore.command.Command;
import btscore.workspace.ZoomService;

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

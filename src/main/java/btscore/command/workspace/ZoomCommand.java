package btscore.command.workspace;

import javafx.geometry.Point2D;
import btscore.command.WorkspaceCommand;
import btscore.workspace.WorkspaceController;
import btscore.workspace.ZoomService;

/**
 *
 * @author Joost
 */
public class ZoomCommand implements WorkspaceCommand {

    private final ZoomService zoomService;
    private final double newScale;
    private final Point2D pivotPoint;

    public ZoomCommand(ZoomService zoomService, double newScale, Point2D pivotPoint) {
        this.zoomService = zoomService;
        this.newScale = newScale;
        this.pivotPoint = pivotPoint;
    }

    @Override
    public boolean execute() {
        zoomService.applyZoom(newScale, pivotPoint);
        return true;
    }

}

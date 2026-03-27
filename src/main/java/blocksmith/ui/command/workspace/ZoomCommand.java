package blocksmith.ui.command.workspace;

import javafx.geometry.Point2D;
import blocksmith.app.workspace.WorkspaceCommand;
import blocksmith.ui.workspace.WorkspaceController;
import blocksmith.ui.workspace.ZoomService;

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
        zoomService.zoomToPoint(newScale, pivotPoint);
        return true;
    }

}

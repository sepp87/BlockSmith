package blocksmith.ui.editor.navigation.command;

import javafx.geometry.Point2D;
import blocksmith.app.command.WorkspaceCommand;
import blocksmith.ui.workspace.WorkspaceController;
import blocksmith.ui.editor.navigation.ZoomService;

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

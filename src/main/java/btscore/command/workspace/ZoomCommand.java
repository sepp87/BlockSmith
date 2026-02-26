package btscore.command.workspace;

import javafx.geometry.Point2D;
import btscore.command.WorkspaceCommand;
import btscore.workspace.WorkspaceController;

/**
 *
 * @author Joost
 */
public class ZoomCommand implements WorkspaceCommand {

    private final WorkspaceController workspace;
    private final double newScale;
    private final Point2D pivotPoint;

    public ZoomCommand(WorkspaceController workspace, double newScale, Point2D pivotPoint) {
        this.workspace = workspace;
        this.newScale = newScale;
        this.pivotPoint = pivotPoint;
    }

    @Override
    public boolean execute() {
        workspace.applyZoom(newScale, pivotPoint);
        return true;
    }

}

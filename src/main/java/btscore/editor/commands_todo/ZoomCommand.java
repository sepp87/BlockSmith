package btscore.editor.commands_todo;

import javafx.geometry.Point2D;
import btscore.editor.context.Command;
import btscore.workspace.WorkspaceContext;
import btscore.workspace.WorkspaceController;

/**
 *
 * @author Joost
 */
public class ZoomCommand implements Command {

    private final WorkspaceController workspace;
    private final double newScale;
    private final Point2D pivotPoint;

    public ZoomCommand(WorkspaceController workspace, double newScale, Point2D pivotPoint) {
        this.workspace = workspace;
        this.newScale = newScale;
        this.pivotPoint = pivotPoint;
    }

    @Override
    public boolean execute(WorkspaceContext context) {
        workspace.applyZoom(newScale, pivotPoint);
        return true;
    }

}

package btscore.editor.commands_done;

import javafx.geometry.Point2D;
import btscore.editor.context.Command;
import btscore.workspace.WorkspaceContext;
import btscore.workspace.WorkspaceController;

/**
 *
 * @author Joost
 */
public class RectangleSelectCommand implements Command {

    private final WorkspaceController workspaceController;
    private final Point2D selectionMin;
    private final Point2D selectionMax;

    public RectangleSelectCommand(WorkspaceController workspaceController, Point2D selectionMin, Point2D selectionMax) {
        this.workspaceController = workspaceController;
        this.selectionMin = workspaceController.getView().sceneToLocal(selectionMin);
        this.selectionMax = workspaceController.getView().sceneToLocal(selectionMax);
    }

    @Override
    public boolean execute(WorkspaceContext context) {
        workspaceController.rectangleSelect(selectionMin, selectionMax);
        return true;

    }

}

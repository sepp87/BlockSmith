package btscore.command.workspace;

import javafx.geometry.Point2D;
import btscore.command.WorkspaceCommand;
import btscore.workspace.WorkspaceController;
import btscore.workspace.WorkspaceSession;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;

/**
 *
 * @author Joost
 */
public class RectangleSelectCommand implements WorkspaceCommand {

    private final WorkspaceSession workspace;
    private final Bounds rectOnWorkspace;

    public RectangleSelectCommand(WorkspaceSession workspace, Bounds rectOnWorkspace) {
        this.workspace = workspace;
        this.rectOnWorkspace = rectOnWorkspace;
    }

    @Override
    public boolean execute() {

        workspace.selectionService().rectangleSelect(rectOnWorkspace);
        return true;

    }

}

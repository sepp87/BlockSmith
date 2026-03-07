package blocksmith.ui.command.workspace;

import blocksmith.ui.command.WorkspaceCommand;
import blocksmith.ui.workspace.SelectionService;
import javafx.geometry.Bounds;

/**
 *
 * @author Joost
 */
public class RectangleSelectCommand implements WorkspaceCommand {

    private final SelectionService selection;
    private final Bounds rectOnWorkspace;

    public RectangleSelectCommand(SelectionService selection, Bounds rectOnWorkspace) {
        this.selection = selection;
        this.rectOnWorkspace = rectOnWorkspace;
    }

    @Override
    public boolean execute() {

        selection.rectangleSelect(rectOnWorkspace);
        return true;

    }

}

package blocksmith.ui.editor.selection.command;

import blocksmith.app.command.WorkspaceCommand;
import blocksmith.ui.editor.selection.SelectionService;
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

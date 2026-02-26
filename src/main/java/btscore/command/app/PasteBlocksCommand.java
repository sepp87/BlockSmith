package btscore.command.app;

import javafx.geometry.Point2D;
import btscore.workspace.WorkspaceController;
import btscore.workspace.WorkspaceModel;
import btscore.command.WorkspaceCommand;

/**
 *
 * @author JoostMeulenkamp
 */
public class PasteBlocksCommand implements WorkspaceCommand {

    private final WorkspaceController workspaceController;
    private final WorkspaceModel workspaceModel;
    private final Point2D pastePoint;

    public PasteBlocksCommand(WorkspaceController workspaceController, WorkspaceModel workspaceModel, Point2D pastePoint) {
        this.workspaceController = workspaceController;
        this.workspaceModel = workspaceModel;
        this.pastePoint = pastePoint;
    }

    @Override
    public boolean execute() {

        var blocks = workspaceModel.graphEditor().pasteBlocks();
        workspaceController.deselectAllBlocks();
        blocks.forEach(b -> workspaceController.updateSelection(b.toString(), true));
        
        return true;
    }

}

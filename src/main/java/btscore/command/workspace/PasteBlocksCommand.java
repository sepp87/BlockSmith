package btscore.command.workspace;

import javafx.geometry.Point2D;
import btscore.workspace.WorkspaceModel;
import btscore.command.WorkspaceCommand;

/**
 *
 * @author JoostMeulenkamp
 */
public class PasteBlocksCommand implements WorkspaceCommand {

    private final WorkspaceModel workspaceModel;
    private final Point2D pastePoint;

    public PasteBlocksCommand( WorkspaceModel workspaceModel, Point2D pastePoint) {
        this.workspaceModel = workspaceModel;
        this.pastePoint = pastePoint;
    }

    @Override
    public boolean execute() {

        var blocks = workspaceModel.graphEditor().pasteBlocks();
        workspaceModel.selectionModel().select(blocks);
        
        return true;
    }

}

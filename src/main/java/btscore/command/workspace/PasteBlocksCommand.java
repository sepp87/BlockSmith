package btscore.command.workspace;

import javafx.geometry.Point2D;
import btscore.workspace.WorkspaceSession;
import btscore.command.WorkspaceCommand;

/**
 *
 * @author JoostMeulenkamp
 */
public class PasteBlocksCommand implements WorkspaceCommand {

    private final WorkspaceSession workspaceModel;
    private final Point2D pastePoint;

    public PasteBlocksCommand( WorkspaceSession workspaceModel, Point2D pastePoint) {
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

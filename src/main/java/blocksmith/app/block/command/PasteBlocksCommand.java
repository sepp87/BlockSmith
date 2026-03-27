package blocksmith.app.block.command;

import javafx.geometry.Point2D;
import blocksmith.app.workspace.WorkspaceSession;
import blocksmith.app.workspace.WorkspaceCommand;

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
        workspaceModel.selection().select(blocks);
        
        return true;
    }

}

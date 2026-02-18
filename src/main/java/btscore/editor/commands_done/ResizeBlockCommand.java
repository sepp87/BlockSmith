package btscore.editor.commands_done;

import blocksmith.domain.block.BlockId;
import btscore.graph.block.BlockController;
import btscore.editor.context.UndoableCommand;
import btscore.workspace.WorkspaceContext;
import btscore.workspace.WorkspaceModel;

/**
 *
 * @author JoostMeulenkamp
 */
public class ResizeBlockCommand implements UndoableCommand {

    private final WorkspaceModel workspaceModel;
    private final BlockController blockController;
    private final double previousWidth;
    private final double previousHeight;
    private final double currentWidth;
    private final double currentHeight;

    public ResizeBlockCommand(WorkspaceModel workspaceModel, BlockController blockController, double width, double height) {
        this.workspaceModel = workspaceModel;
        this.blockController = blockController;
        this.previousWidth = blockController.getPreviousWidth();
        this.previousHeight = blockController.getPreviousHeight();
        this.currentWidth = width;
        this.currentHeight = height;
    }

    @Override
    public boolean execute(WorkspaceContext context) {
        var id = BlockId.from(blockController.getModel().getId());
        workspaceModel.graphEditor().resizeBlock(id, currentWidth, currentHeight);
        blockController.getModel().widthProperty().set(currentWidth);
        blockController.getModel().heightProperty().set(currentHeight);
        return true;

    }

    @Override
    public void undo() {
        blockController.getModel().widthProperty().set(previousHeight);
        blockController.getModel().heightProperty().set(previousWidth);
    }

}

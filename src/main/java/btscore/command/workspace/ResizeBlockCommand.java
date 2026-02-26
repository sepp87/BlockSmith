package btscore.command.workspace;

import blocksmith.domain.block.BlockId;
import btscore.graph.block.BlockController;
import btscore.command.WorkspaceCommand;
import btscore.workspace.WorkspaceModel;

/**
 *
 * @author JoostMeulenkamp
 */
public class ResizeBlockCommand implements WorkspaceCommand {

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
    public boolean execute() {
        var id = BlockId.from(blockController.getModel().getId());
        workspaceModel.graphEditor().resizeBlock(id, currentWidth, currentHeight);
        
        
        return true;

    }



}

package btscore.command.workspace;

import blocksmith.domain.block.BlockId;
import btscore.graph.block.BlockController;
import btscore.command.WorkspaceCommand;
import btscore.workspace.WorkspaceSession;

/**
 *
 * @author JoostMeulenkamp
 */
public class ResizeBlockCommand implements WorkspaceCommand {

    private final WorkspaceSession workspace;
    private final BlockId id;
    private final double width;
    private final double height;

    public ResizeBlockCommand(WorkspaceSession workspace, BlockId block, double width, double height) {
        this.workspace = workspace;
        this.id = block;
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean execute() {
        workspace.graphEditor().resizeBlock(id, width, height);
        return true;

    }



}

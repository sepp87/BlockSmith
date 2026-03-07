package blocksmith.ui.command.workspace;

import blocksmith.domain.block.BlockId;
import blocksmith.ui.graph.block.BlockController;
import blocksmith.ui.command.WorkspaceCommand;
import blocksmith.ui.workspace.WorkspaceSession;

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

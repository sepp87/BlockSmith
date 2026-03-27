package blocksmith.app.block.command;

import blocksmith.app.workspace.WorkspaceSession;
import blocksmith.app.command.WorkspaceCommand;

/**
 *
 * @author JoostMeulenkamp
 */
public class PasteBlocksCommand implements WorkspaceCommand {

    private final WorkspaceSession workspaceModel;
    private final Double x;
    private final Double y;

    public PasteBlocksCommand(WorkspaceSession workspaceModel, Double x, Double y) {
        this.workspaceModel = workspaceModel;
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean execute() {

        var blocks = workspaceModel.graphEditor().pasteBlocks();
        workspaceModel.selection().select(blocks);

        return true;
    }

}

package blocksmith.app.block.command;

import blocksmith.app.workspace.WorkspaceSession;
import blocksmith.app.command.WorkspaceCommand;

/**
 *
 * @author Joost
 */
public class AddBlockCommand implements WorkspaceCommand {

    private final WorkspaceSession session;
    private final String blockType;
    private final double x;
    private final double y;

    public AddBlockCommand(WorkspaceSession session, String blockType, double x, double y) {
        this.session = session;
        this.blockType = blockType;
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean execute() {
        session.graphEditor().addBlock(blockType, x, y);
        return true;
    }

}

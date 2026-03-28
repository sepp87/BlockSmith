package blocksmith.app.block.command;

import blocksmith.app.command.WorkspaceCommand;
import blocksmith.app.workspace.WorkspaceSession;

/**
 *
 * @author Joost
 */
public class SelectAllBlocksCommand implements WorkspaceCommand {

    private final WorkspaceSession session;

    public SelectAllBlocksCommand(WorkspaceSession session) {
        this.session = session;
    }

    @Override
    public boolean execute() {
        session.selection().selectAll();
        return true;
    }


}

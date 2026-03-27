package blocksmith.app.block.command;

import blocksmith.app.workspace.WorkspaceSession;
import blocksmith.app.command.WorkspaceCommand;

/**
 *
 * @author Joost
 */
public class RemoveBlocksCommand implements WorkspaceCommand {

    private final WorkspaceSession session;

    public RemoveBlocksCommand( WorkspaceSession session) {
        this.session = session;
    }

    @Override
    public boolean execute() {

        var ids = session.selection().selected();
        session.graphEditor().removeAllBlocks(ids);
        session.selection().deselectAll();

        return true;

    }

}

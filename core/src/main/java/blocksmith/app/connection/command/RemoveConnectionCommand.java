package blocksmith.app.connection.command;

import blocksmith.domain.connection.Connection;
import blocksmith.app.workspace.WorkspaceSession;
import blocksmith.app.command.WorkspaceCommand;

/**
 *
 * @author Joost
 */
public class RemoveConnectionCommand implements WorkspaceCommand {

    private final WorkspaceSession workspace;
    private final Connection connection;

    public RemoveConnectionCommand(WorkspaceSession workspace, Connection connection) {
        this.workspace = workspace;
        this.connection = connection;
    }

    @Override
    public boolean execute() {
        workspace.graphEditor().removeConnection(connection);
        return true;

    }

}

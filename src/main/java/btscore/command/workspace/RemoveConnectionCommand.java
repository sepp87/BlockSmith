package btscore.command.workspace;

import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.Connection;
import blocksmith.domain.connection.PortRef;
import btscore.graph.connection.ConnectionModel;
import btscore.workspace.WorkspaceSession;
import btscore.command.WorkspaceCommand;

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

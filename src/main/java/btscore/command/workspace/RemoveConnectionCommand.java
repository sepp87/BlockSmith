package btscore.command.workspace;

import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.Connection;
import blocksmith.domain.connection.PortRef;
import btscore.graph.connection.ConnectionModel;
import btscore.workspace.WorkspaceModel;
import btscore.command.WorkspaceCommand;

/**
 *
 * @author Joost
 */
public class RemoveConnectionCommand implements WorkspaceCommand {

    private final WorkspaceModel workspace;
    private final Connection connection;

    public RemoveConnectionCommand(WorkspaceModel workspace, Connection connection) {
        this.workspace = workspace;
        this.connection = connection;
    }

    @Override
    public boolean execute() {
        workspace.graphEditor().removeConnection(connection);
        return true;

    }

}

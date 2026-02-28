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


    private final WorkspaceModel workspaceModel;
    private final ConnectionModel connection;
    private ConnectionModel autoConnection;

    public RemoveConnectionCommand(WorkspaceModel workspaceModel, ConnectionModel connection) {
        this.workspaceModel = workspaceModel;
        this.connection = connection;
    }

    @Override
    public boolean execute() {
        var from = PortRef.of(
                BlockId.from(connection.getStartPort().getBlock().getId()),
                connection.getStartPort().labelProperty().get()
        );
        var to = PortRef.of(
                BlockId.from(connection.getEndPort().getBlock().getId()),
                connection.getEndPort().labelProperty().get()
        );
        var domain = new Connection(from, to);
        workspaceModel.graphEditor().removeConnection(domain);

        return true;

    }

}

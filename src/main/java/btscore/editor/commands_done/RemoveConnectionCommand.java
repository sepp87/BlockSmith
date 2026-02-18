package btscore.editor.commands_done;

import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.Connection;
import blocksmith.domain.connection.PortRef;
import btscore.graph.connection.ConnectionModel;
import btscore.workspace.WorkspaceModel;
import btscore.editor.context.UndoableCommand;
import btscore.graph.port.PortModel;
import btscore.workspace.WorkspaceContext;

/**
 *
 * @author Joost
 */
public class RemoveConnectionCommand implements UndoableCommand {


    private final WorkspaceModel workspaceModel;
    private final ConnectionModel connection;
    private ConnectionModel autoConnection;

    public RemoveConnectionCommand(WorkspaceModel workspaceModel, ConnectionModel connection) {
        this.workspaceModel = workspaceModel;
        this.connection = connection;
    }

    @Override
    public boolean execute(WorkspaceContext context) {
        var from = PortRef.of(
                BlockId.from(connection.getStartPort().getBlock().getId()),
                connection.getStartPort().nameProperty().get()
        );
        var to = PortRef.of(
                BlockId.from(connection.getEndPort().getBlock().getId()),
                connection.getEndPort().nameProperty().get()
        );
        var domain = new Connection(from, to);
        workspaceModel.graphEditor().removeConnection(domain);

        // OLD STUFF
        workspaceModel.removeConnectionModel(connection);
        if (connection.getEndPort().autoConnectableProperty().get()) {
            PortModel port = connection.getEndPort();
            ConnectionModel newConnection = workspaceModel.getAutoConnectIndex().registerReceiver(port);
            if (newConnection != null) {
                this.autoConnection = newConnection;
                workspaceModel.addConnectionModel(newConnection);
            }
        }
        return true;

    }

    @Override
    public void undo() {
        if (autoConnection != null) {
            workspaceModel.removeConnectionModel(autoConnection);
            autoConnection = null;
        }
        connection.revive();
        workspaceModel.addConnectionModel(connection);
    }
}

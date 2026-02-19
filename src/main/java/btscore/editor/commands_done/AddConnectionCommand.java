package btscore.editor.commands_done;

import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.PortRef;
import btscore.Launcher;
import java.util.HashSet;
import java.util.Set;
import btscore.graph.connection.ConnectionModel;
import btscore.graph.port.PortModel;
import btscore.workspace.WorkspaceModel;
import btscore.editor.context.UndoableCommand;
import btscore.workspace.WorkspaceContext;

/**
 *
 * TODO Not yet implemented
 *
 * @author Joost
 */
public class AddConnectionCommand implements UndoableCommand {

    private final WorkspaceModel workspaceModel;
    private final PortModel fromPort;
    private final PortModel toPort;
    private final Set<ConnectionModel> removedConnections;
    private ConnectionModel newConnection;

    public AddConnectionCommand(WorkspaceModel workspaceModel, PortModel fromPort, PortModel toPort) {
        this.workspaceModel = workspaceModel;
        this.fromPort = fromPort;
        this.toPort = toPort;
        this.removedConnections = new HashSet<>();
    }

    @Override
    public boolean execute(WorkspaceContext context) {

        var from = new PortRef(
                BlockId.from(fromPort.getBlock().getId()),
                fromPort.nameProperty().get()
        );
        var to = new PortRef(
                BlockId.from(toPort.getBlock().getId()),
                toPort.nameProperty().get()
        );
        workspaceModel.graphEditor().addConnection(from, to);

        if (Launcher.DOMAIN_GRAPH) {
            return true;
        }

        // OLD STUFF
        System.out.println("CreateConnectionCommand.execute()");
        if (!toPort.isMultiDockAllowed()) { // remove all connections for the receiving (INPUT) port if multi dock is NOT allowed
            Set<ConnectionModel> connections = toPort.getConnections();
            removedConnections.addAll(connections);
            for (ConnectionModel connection : connections) {
                workspaceModel.removeConnectionModel(connection);
            }
        }

        if (newConnection == null) { // create the new connection
            newConnection = workspaceModel.addConnectionModel(fromPort, toPort);
        } else { // revive the connection, because it was undone
            newConnection.revive();
            workspaceModel.addConnectionModel(newConnection);
        }
        return true;
    }

    @Override
    public void undo() {

        if (Launcher.DOMAIN_GRAPH) {
            return;
        }

        workspaceModel.removeConnectionModel(newConnection);
        for (ConnectionModel connection : removedConnections) {
            connection.revive();
            workspaceModel.addConnectionModel(connection);
        }
    }
}

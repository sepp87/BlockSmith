package btscore.command.workspace;

import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.PortRef;
import java.util.HashSet;
import java.util.Set;
import btscore.graph.connection.ConnectionModel;
import btscore.graph.port.PortModel;
import btscore.workspace.WorkspaceModel;
import btscore.command.WorkspaceCommand;

/**
 *
 * TODO Not yet implemented
 *
 * @author Joost
 */
public class AddConnectionCommand implements WorkspaceCommand {

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
    public boolean execute() {

        var from = new PortRef(
                BlockId.from(fromPort.getBlock().getId()),
                fromPort.nameProperty().get()
        );
        var to = new PortRef(
                BlockId.from(toPort.getBlock().getId()),
                toPort.nameProperty().get()
        );
        workspaceModel.graphEditor().addConnection(from, to);

        return true;
    }

}

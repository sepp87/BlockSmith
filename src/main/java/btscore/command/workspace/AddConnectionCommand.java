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

    private final WorkspaceModel workspace;
    private final PortRef from;
    private final PortRef to;

    public AddConnectionCommand(WorkspaceModel workspace, PortRef from, PortRef to) {
        this.workspace = workspace;
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean execute() {
        workspace.graphEditor().addConnection(from, to);
        return true;
    }

}

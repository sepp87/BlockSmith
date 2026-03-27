package blocksmith.app.connection.command;

import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.PortRef;
import java.util.HashSet;
import java.util.Set;
import blocksmith.ui.graph.connection.ConnectionModel;
import blocksmith.ui.graph.port.PortModel;
import blocksmith.app.workspace.WorkspaceSession;
import blocksmith.app.workspace.WorkspaceCommand;

/**
 *
 * TODO Not yet implemented
 *
 * @author Joost
 */
public class AddConnectionCommand implements WorkspaceCommand {

    private final WorkspaceSession workspace;
    private final PortRef from;
    private final PortRef to;

    public AddConnectionCommand(WorkspaceSession workspace, PortRef from, PortRef to) {
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

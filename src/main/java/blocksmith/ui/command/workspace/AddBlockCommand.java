package blocksmith.ui.command.workspace;

import blocksmith.ui.graph.block.BlockModelFactory;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point2D;
import blocksmith.ui.graph.block.BlockModel;
import blocksmith.ui.workspace.WorkspaceSession;
import blocksmith.ui.graph.connection.ConnectionModel;
import blocksmith.ui.command.WorkspaceCommand;

/**
 *
 * @author Joost
 */
public class AddBlockCommand implements WorkspaceCommand {

    private final WorkspaceSession session;
    private final String blockType;
    private final Point2D location;
    private BlockModel blockModel;
    private final List<ConnectionModel> wirelessConnections = new ArrayList<>();

    public AddBlockCommand(WorkspaceSession session, String blockType, Point2D location) {
        this.session = session;
        this.blockType = blockType;
        this.location = location;

    }

    @Override
    public boolean execute() {
        session.graphEditor().addBlock(blockType, location.getX(), location.getY());

        return true;
    }


}

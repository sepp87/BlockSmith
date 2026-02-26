package btscore.command.workspace;

import blocksmith.ui.BlockModelFactory;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point2D;
import btscore.graph.block.BlockModel;
import btscore.workspace.WorkspaceModel;
import btscore.graph.connection.ConnectionModel;
import btscore.command.WorkspaceCommand;

/**
 *
 * @author Joost
 */
public class AddBlockCommand implements WorkspaceCommand {

    private final BlockModelFactory blockModelFactory;
    private final WorkspaceModel workspaceModel;
    private final String blockType;
    private final Point2D location;
    private BlockModel blockModel;
    private final List<ConnectionModel> wirelessConnections = new ArrayList<>();

    public AddBlockCommand(BlockModelFactory blockModelFactory, WorkspaceModel workspaceModel, String blockType, Point2D location) {
        this.blockModelFactory = blockModelFactory;
        this.workspaceModel = workspaceModel;
        this.blockType = blockType;
        this.location = location;

    }

    @Override
    public boolean execute() {
        workspaceModel.graphEditor().addBlock(blockType, location.getX(), location.getY());

        return true;
    }


}

package btscore.editor.commands_done;

import blocksmith.domain.block.BlockLayout;
import blocksmith.ui.BlockModelFactory;
import btscore.Launcher;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point2D;
import btscore.graph.block.BlockFactory;
import btscore.graph.block.BlockModel;
import btscore.workspace.WorkspaceModel;
import btscore.editor.context.UndoableCommand;
import btscore.graph.connection.ConnectionModel;
import btscore.graph.port.PortModel;
import btscore.workspace.WorkspaceContext;

/**
 *
 * @author Joost
 */
public class AddBlockCommand implements UndoableCommand {

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
    public boolean execute(WorkspaceContext context) {
        workspaceModel.addBlock(blockType, BlockLayout.create(location.getX(), location.getY()));

        // OLD STUFF
        if (blockModel == null) {

            if (Launcher.BLOCK_DEF_LOADER) {
                blockModel = blockModelFactory.create(blockType);
            } else {
                blockModel = BlockFactory.createBlock(blockType);
            }

            blockModel.layoutXProperty().set(location.getX());
            blockModel.layoutYProperty().set(location.getY());

        } else { // redo triggered
            blockModel.revive();
        }

        workspaceModel.addBlockModel(blockModel);

        // auto-connect transmitters
        wirelessConnections.clear();
        List<PortModel> transmitters = blockModel.getTransmittingPorts();
        for (PortModel port : transmitters) {
            List<ConnectionModel> autoConnections = workspaceModel.getAutoConnectIndex().registerTransmitter(port);
            wirelessConnections.addAll(autoConnections);
        }

        // auto-connect receivers
        List<PortModel> receivers = blockModel.getReceivingPorts();
        for (PortModel port : receivers) {
            ConnectionModel autoConnection = workspaceModel.getAutoConnectIndex().registerReceiver(port);
            if (autoConnection != null) {
                wirelessConnections.add(autoConnection);
            }
        }

        // place wireless connections on the workspace
        for (ConnectionModel connection : wirelessConnections) {
            workspaceModel.addConnectionModel(connection);
        }

        System.out.println("PENDING RECEIVERS " + workspaceModel.getAutoConnectIndex().pendingReceivers.size());

        return true;
    }

    /**
     * Info - there is no need to record the index of the transmitter when
     * removing it from the registry, since this newly added block is always
     * last in the list
     */
    @Override
    public void undo() {

        // first unregister all transmitters, so receivers won't auto-connect to them again 
        List<PortModel> transmitters = blockModel.getTransmittingPorts();
        for (PortModel port : transmitters) {
            workspaceModel.getAutoConnectIndex().unregisterTransmitter(port);
        }

        // now connected receivers can be safely registered to await a new transmitter, without causing connections with the freshly removed transmitters
        for (PortModel transmitter : transmitters) {
            for (PortModel port : transmitter.getConnectedPorts()) {
                workspaceModel.getAutoConnectIndex().registerReceiver(port);
            }
        }

        // remove all receivers of the block itself
        for (PortModel port : blockModel.getReceivingPorts()) {
            workspaceModel.getAutoConnectIndex().unregisterReceiver(port);

        }

        workspaceModel.removeBlockModel(blockModel);
        for (ConnectionModel connection : wirelessConnections) {
            workspaceModel.removeConnectionModel(connection);
        }

    }

}

package btscore.workspace;

import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.Connection;
import blocksmith.domain.group.GroupId;
import java.util.ArrayList;
import btscore.graph.group.BlockGroupModel;
import btscore.graph.connection.PreConnection;
import btscore.graph.block.BlockController;
import btscore.graph.connection.ConnectionModel;
import btscore.graph.block.BlockView;
import btscore.graph.block.BlockModel;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.SetChangeListener;
import javafx.collections.SetChangeListener.Change;
import btscore.UiApp;
import btscore.graph.BaseController;
import btscore.command.WorkspaceCommandBus;
import btscore.graph.connection.ConnectionController;
import btscore.graph.connection.ConnectionView;
import btscore.graph.group.BlockGroupController;
import btscore.graph.group.BlockGroupView;
import btscore.graph.port.PortController;

/**
 *
 * @author JoostMeulenkamp
 */
public class WorkspaceController extends BaseController {

    private final WorkspaceState state;
    private final WorkspaceSession model;
    private final WorkspaceView view;
    private final GraphProjection projection;

    private final ZoomService zoomService;
    private final InfoPanelHelper infoPanelHelper;

    private final Map<BlockId, BlockController> blockIndex = new HashMap<>();
    private final Map<Connection, ConnectionController> connectionIndex = new HashMap<>();
    private final Map<GroupId, BlockGroupController> groupIndex = new HashMap<>();

    private final Map<BlockModel, BlockController> blocks = new HashMap<>();
    private final Map<ConnectionModel, ConnectionController> connections = new HashMap<>();
    private final Map<BlockGroupModel, BlockGroupController> blockGroups = new HashMap<>();
    private final Map<String, PortController> ports = new HashMap<>();

    public WorkspaceController(WorkspaceCommandBus commands, WorkspaceSession session, WorkspaceState state, WorkspaceView workspaceView, GraphProjection projection) {
        super(commands, session);
        this.state = state;
        this.model = session;
        this.view = workspaceView;
        this.projection = projection;
        this.zoomService = new ZoomService(model, view, projection);
        this.infoPanelHelper = new InfoPanelHelper(view);

        model.getBlockModels().forEach(b -> addBlock(b));
        model.getConnectionModels().forEach(c -> addConnection(c));
        model.getBlockGroupModels().forEach(g -> addBlockGroup(g));

        model.addBlockModelsListener(blockModelsListener);
        model.addConnectionModelsListener(connectionModelsListener);
        model.addBlockGroupModelsListener(blockGroupModelsListener);

        session.selectionModel().addSelectionListener(this::onSelectionChanged);
    }

    public void updateFrom(GraphProjectionDiff diff) {

    }

    public ZoomService zoomService() {
        return zoomService;
    }

    private void onSelectionChanged(Collection<BlockId> ids) {
        for (var entry : blockIndex.entrySet()) {
            var isSelected = ids.contains(entry.getKey());
            entry.getValue().selectedProperty().set(isSelected);
        }
    }

    public void registerPort(PortController portController) {
        ports.put(portController.getModel().idProperty().get(), portController);
    }

    public void unregisterPort(PortController portController) {
        ports.remove(portController.getModel().idProperty().get());
    }

    public PortController getPortController(String id) {
        return ports.get(id);
    }

    /**
     * BLOCKS
     */
    SetChangeListener<BlockModel> blockModelsListener = this::onBlockModelsChanged;

    private void onBlockModelsChanged(Change<? extends BlockModel> change) {
        if (UiApp.LOG_METHOD_CALLS) {
            System.out.println("WorkspaceController.onBlockModelsChanged()");
        }
        if (change.wasAdded()) {
            addBlock(change.getElementAdded());
        } else {
            removeBlock(change.getElementRemoved());
        }
    }

    private void addBlock(BlockModel blockModel) {
        if (UiApp.LOG_METHOD_CALLS) {
            System.out.println("WorkspaceController.addBlock()");
        }
        BlockView blockView = new BlockView();
        view.getBlockLayer().getChildren().add(blockView);
        BlockController blockController = new BlockController(commands, session, projection, this, blockModel, blockView);
        blocks.put(blockModel, blockController);
        blockIndex.put(BlockId.from(blockModel.getId()), blockController);
    }

    private void removeBlock(BlockModel blockModel) {
        if (UiApp.LOG_METHOD_CALLS) {
            System.out.println("WorkspaceController.removeBlock()");
        }
        BlockController blockController = blocks.remove(blockModel);
        blockIndex.remove(BlockId.from(blockModel.getId()));
        view.getBlockLayer().getChildren().remove(blockController.getView());
        blockController.remove();
        // controller remove itself
    }

    /**
     * BLOCK INFO
     */
    public void showInfoPanel(BlockController blockController) {
        infoPanelHelper.showInfoPanel(blockController);
    }

    public void showExceptionPanel(BlockController blockController) {
        infoPanelHelper.showExceptionPanel(blockController);
    }

    /**
     * GROUPS
     */
    SetChangeListener<BlockGroupModel> blockGroupModelsListener = this::onBlockGroupModelsChanged;

    private void onBlockGroupModelsChanged(Change<? extends BlockGroupModel> change) {
        if (change.wasAdded()) {
            addBlockGroup(change.getElementAdded());
        } else {
            removeBlockGroup(change.getElementRemoved());
        }
    }

    private void addBlockGroup(BlockGroupModel blockGroupModel) {
        if (UiApp.LOG_METHOD_CALLS) {
            System.out.println("WorkspaceController.addBlockGroup()");
        }
        BlockGroupView blockGroupView = new BlockGroupView();
        view.getGroupLayer().getChildren().add(0, blockGroupView);
        BlockGroupController blockGroupController = new BlockGroupController(commands, session, state, this, blockGroupModel, blockGroupView);
        List<BlockController> blockControllers = new ArrayList<>();
        for (BlockModel blockModel : blockGroupModel.getBlocks()) {
            blockControllers.add(blockIndex.get(BlockId.from(blockModel.getId())));
        }
        blockGroupController.setBlocks(blockControllers);
        blockGroups.put(blockGroupModel, blockGroupController);
    }

    private void removeBlockGroup(BlockGroupModel blockGroupModel) {
        if (UiApp.LOG_METHOD_CALLS) {
            System.out.println("WorkspaceController.removeBlockGroup()");
        }
        BlockGroupController blockGroupController = blockGroups.remove(blockGroupModel);
        view.getGroupLayer().getChildren().remove(blockGroupController.getView());
        blockGroupController.remove();
    }

    /**
     * CONNECTIONS
     */
    SetChangeListener<ConnectionModel> connectionModelsListener = this::onConnectionModelsChanged;

    private void onConnectionModelsChanged(Change<? extends ConnectionModel> change) {
        if (UiApp.LOG_METHOD_CALLS) {
            System.out.println("WorkspaceController.onConnectionModelsChanged()");
        }
        if (change.wasAdded()) {
            addConnection(change.getElementAdded());
        } else {
            removeConnection(change.getElementRemoved());
        }
    }

    private void addConnection(ConnectionModel connectionModel) {
        if (UiApp.LOG_METHOD_CALLS) {
            System.out.println("WorkspaceController.addConnection()");
        }
        ConnectionView connectionView = new ConnectionView();
        view.getConnectionLayer().getChildren().add(connectionView);

        ConnectionController connectionController = new ConnectionController(commands, session, this, connectionModel, connectionView);
        connections.put(connectionModel, connectionController);
    }

    private void removeConnection(ConnectionModel connectionModel) {
        if (UiApp.LOG_METHOD_CALLS) {
            System.out.println("WorkspaceController.removeConnection()");
        }
        ConnectionController connectionController = connections.remove(connectionModel);
        view.getConnectionLayer().getChildren().remove(connectionController.getView());
        connectionController.remove();

    }

    private PreConnection preConnection = null;

    // rename to initiateConnection and when PreConnection != null, then turn PreConnection into a real connection
    public void initiateConnection(PortController portController) {
        if (UiApp.LOG_METHOD_CALLS) {
            System.out.println("WorkspaceController.initiateConnection()");
        }
        if (preConnection == null) {
            preConnection = new PreConnection(commands, session, WorkspaceController.this, portController);
            view.getConnectionLayer().getChildren().add(0, preConnection);
        }
    }

    // method is unneeded if createConnection catches the second click
    public void removePreConnection(PreConnection preConnection) {
        if (UiApp.LOG_METHOD_CALLS) {
            System.out.println("WorkspaceController.removePreConnection()");
        }
        view.getConnectionLayer().getChildren().remove(preConnection);
        this.preConnection = null;
    }

    /**
     * GETTERS
     */
    public WorkspaceSession getModel() {
        return model;
    }

    public WorkspaceView getView() {
        return view;
    }

    public BlockController getBlockController(BlockModel blockModel) {
        return blockIndex.get(BlockId.from(blockModel.getId()));
    }

}

package blocksmith.ui.workspace;

import blocksmith.app.workspace.WorkspaceSession;
import blocksmith.ui.projection.GraphProjectionDiff;
import blocksmith.ui.projection.GraphProjection;
import blocksmith.ui.view.GraphFxMapper;
import blocksmith.domain.block.Block;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.Connection;
import blocksmith.domain.group.Group;
import blocksmith.domain.group.GroupId;
import blocksmith.ui.graph.connection.PreConnection;
import blocksmith.ui.graph.block.BlockController;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import blocksmith.ui.UiApp;
import blocksmith.ui.graph.BaseController;
import blocksmith.app.workspace.WorkspaceCommandBus;
import blocksmith.ui.graph.connection.ConnectionController;
import blocksmith.ui.graph.group.BlockGroupController;
import blocksmith.ui.graph.port.PortController;

/**
 *
 * @author JoostMeulenkamp
 */
public class WorkspaceController extends BaseController {

    private final WorkspaceSession model;
    private final WorkspaceView view;
    private final GraphFxMapper mapper;

    private final InfoPanelHelper infoPanelHelper;

    private final Map<BlockId, BlockController> blocks = new HashMap<>();
    private final Map<Connection, ConnectionController> connections = new HashMap<>();
    private final Map<GroupId, BlockGroupController> groups = new HashMap<>();

    private final Map<String, PortController> ports = new HashMap<>();

    public WorkspaceController(WorkspaceCommandBus commands, WorkspaceSession session, WorkspaceState state, WorkspaceView workspaceView, GraphProjection projection) {
        super(commands, session);
        this.model = session;
        this.view = workspaceView;
        this.mapper = new GraphFxMapper(session, commands, projection, this, state);
        this.infoPanelHelper = new InfoPanelHelper(view);

        var graph = session.graphSnapshot();
        addBlocks(graph.blocks().stream().map(Block::id).toList());
        addConnections(graph.connections());
        addGroups(graph.groups().stream().map(Group::id).toList());
        

        session.selection().addSelectionListener(this::onSelectionChanged);
    }

    public void updateFrom(GraphProjectionDiff diff) {

        for (var id : diff.removedGroups()) {
            var controller = groups.remove(id);
            view.getGroupLayer().getChildren().remove(controller.getView());
            controller.dispose();
        }
        for (var id : diff.removedConnections()) {
            var controller = connections.remove(id);
            view.getConnectionLayer().getChildren().remove(controller.getView());
            controller.dispose();
        }

        for (var id : diff.removedBlocks()) {
            var controller = blocks.remove(id);
            view.getBlockLayer().getChildren().remove(controller.getView());
            controller.dispose();
        }

        addBlocks(diff.addedBlocks());
        addConnections(diff.addedConnections());
        addGroups(diff.addedGroups());

    }

    private void addBlocks(Collection<BlockId> ids) {
        var layer = view.getBlockLayer();
        var entries = mapper.blocksToFx(ids);
        blocks.putAll(entries);
        var views = entries.values().stream().map(e -> e.getView()).toList();
        layer.getChildren().addAll(views);
    }

    private void addConnections(Collection<Connection> ids) {
        var layer = view.getConnectionLayer();
        var entries = mapper.connectionsToFx(ids);
        connections.putAll(entries);
        var views = entries.values().stream().map(e -> e.getView()).toList();
        layer.getChildren().addAll(views);
    }

    private void addGroups(Collection<GroupId> ids) {
        var layer = view.getGroupLayer();
        var entries = mapper.groupsToFx(ids, blocks);
        groups.putAll(entries);
        var views = entries.values().stream().map(e -> e.getView()).toList();
        layer.getChildren().addAll(views);
    }

    private void onSelectionChanged(Collection<BlockId> ids) {
        for (var entry : blocks.entrySet()) {
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
     * BLOCK INFO
     */
    public void showInfoPanel(BlockController blockController) {
        infoPanelHelper.showInfoPanel(blockController);
    }

    public void showExceptionPanel(BlockController blockController) {
        infoPanelHelper.showExceptionPanel(blockController);
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

    public BlockController getBlockController(BlockId block) {
        return blocks.get(block);
    }

}

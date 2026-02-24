package btscore.workspace;

import blocksmith.app.GraphDocument;
import blocksmith.app.GraphEditorFactory;
import blocksmith.app.inbound.GraphEditor;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.Connection;
import blocksmith.domain.connection.PortRef;
import blocksmith.domain.graph.Graph;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import btscore.graph.group.BlockGroupModel;
import btscore.graph.connection.ConnectionModel;
import btscore.graph.block.BlockModel;
import btscore.graph.port.PortModel;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import btscore.UiApp;
import java.util.Objects;
import blocksmith.app.inbound.GraphMutationAndHistory;
import blocksmith.app.logging.GraphLogFmt;
import blocksmith.domain.block.Block;
import blocksmith.domain.graph.GraphDiff;
import blocksmith.domain.group.Group;
import blocksmith.ui.BlockModelFactory;
import blocksmith.ui.MethodBlockNew;
import blocksmith.ui.workspace.SaveDocument;
import btscore.Launcher;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author Joost
 */
public class WorkspaceModel {

    private final static Logger LOGGER = Logger.getLogger(WorkspaceModel.class.getName());

    private final GraphEditor editor;
    private final BlockModelFactory blockFactory;
    private final SaveDocument saveDocument;
    private GraphDocument document;
    private Path documentPath;
    private List<Consumer<Path>> documentPathListeners = new ArrayList<>();

    private WorkspaceModel(Path documentPath, GraphDocument document, GraphEditorFactory editorFactory, BlockModelFactory blockFactory, SaveDocument saveDocument) {
        this.documentPath = documentPath;
        this.document = document;
        this.editor = editorFactory.createDefault(document.graph());
        this.zoomFactor.set(document.zoomFactor());
        this.translateX.set(document.translateX());
        this.translateY.set(document.translateY());
        this.blockFactory = blockFactory;
        this.saveDocument = saveDocument;

        editor.setOnGraphUpdated(this::updateFrom);

        var graph = editor.currentGraph();
        addBlocksFromDomain(graph.blocks());
        var blockIndex = blockModels.stream().collect(Collectors.toMap(b -> BlockId.from(b.getId()), Function.identity()));
        addConnectionsFromDomain(graph.connections(), blockIndex);
        addGroupsFromDomain(graph.groups(), blockIndex);
    }

    public static WorkspaceModel newDocument(
            GraphEditorFactory editorFactory,
            BlockModelFactory blockFactory,
            SaveDocument saveDocument
    ) {
        Path path = null;
        var document = GraphDocument.createEmpty();
        return new WorkspaceModel(path, document, editorFactory, blockFactory, saveDocument);
    }

    public static WorkspaceModel openDocument(
            Path path,
            GraphDocument document,
            GraphEditorFactory editorFactory,
            BlockModelFactory blockFactory,
            SaveDocument saveDocument
    ) {
        return new WorkspaceModel(path, document, editorFactory, blockFactory, saveDocument);
    }

    private void updateFrom(Graph oldGraph, Graph newGraph) {

        var diff = GraphDiff.compare(oldGraph, newGraph);
        LOGGER.log(Level.INFO, diff.toString());

        if (!Launcher.DOMAIN_GRAPH) {
            return;
        }

        // remove connections
        var connectionIndex = connectionModels.stream().collect(Collectors.toMap(c -> {
            var fromBlock = BlockId.from(c.getStartPort().getBlock().getId());
            var fromPort = c.getStartPort().nameProperty().get();
            var toBlock = BlockId.from(c.getEndPort().getBlock().getId());
            var toPort = c.getEndPort().nameProperty().get();
            var from = new PortRef(fromBlock, fromPort);
            var to = new PortRef(toBlock, toPort);
            var key = new Connection(from, to);
            return key;
        }, c -> c));
        for (var connection : diff.removedConnections()) {
            var model = connectionIndex.get(connection);
            removeConnectionModel(model);
        }

        // remove blocks
        var blockIndex = blockModels.stream().collect(Collectors.toMap(b -> BlockId.from(b.getId()), Function.identity()));
        for (var block : diff.removedBlocks()) {
            var model = blockIndex.get(block.id());
            removeBlockModel(model);
        }

        // add blocks
        addBlocksFromDomain(diff.addedBlocks());

        // update blocks
        for (var block : diff.updatedBlocks()) {
            var model = blockIndex.get(block.id());
            if (model instanceof MethodBlockNew mbn) {
                mbn.updateFrom(block);
            }
        }

        // add connections
        blockIndex = blockModels.stream().collect(Collectors.toMap(b -> BlockId.from(b.getId()), Function.identity()));
        addConnectionsFromDomain(diff.addedConnections(), blockIndex);

        // remove connections
        // remove blocks
        // create blocks 
        // update blocks
        // create connections
    }

    private void addBlocksFromDomain(Collection<Block> blocks) {
        for (var block : blocks) {
            var model = blockFactory.create(block.type(), block.id().toString());
            model.updateFrom(block);
            addBlockModel(model);
        }
    }

    private void addConnectionsFromDomain(Collection<Connection> connections, Map<BlockId, BlockModel> blockIndex) {
        for (var connection : connections) {

            System.out.println("rOUTPUT " + GraphLogFmt.port(connection.from()));
            var fromBlock = blockIndex.get(connection.from().blockId());
            var fromPort = fromBlock.getOutputPorts().stream()
                    .filter(p -> p.nameProperty().get().equals(connection.from().valueId()))
                    .findFirst()
                    .get();
            var toBlock = blockIndex.get(connection.to().blockId());
            var toPort = toBlock.getInputPorts().stream()
                    .filter(p -> p.nameProperty().get().equals(connection.to().valueId()))
                    .findFirst()
                    .get();

            var model = new ConnectionModel(fromPort, toPort);
            addConnectionModel(model);
        }
    }

    private void addGroupsFromDomain(Collection<Group> groups, Map<BlockId, BlockModel> blockIndex) {
        for (var group : groups) {
            var model = new BlockGroupModel(blockGroupIndex);
            model.nameProperty().set(group.label());
            group.blocks().forEach(b -> model.addBlock(blockIndex.get(b)));
            addBlockGroupModel(model);
        }
    }

    public GraphMutationAndHistory graphEditor() {
        return editor;
    }

    public boolean isSaved() {
        return Objects.equals(editor.currentGraph(), document.graph());
    }

    public void saveDocument(Path path) throws Exception {
        var graph = editor.currentGraph();
        var newVersion = new GraphDocument(graph, zoomFactor.get(), translateX.get(), translateY.get());
        saveDocument.execute(path, newVersion);
        document = newVersion;
        if (!Objects.equals(documentPath, path)) {
            documentPath = path;
            onDocumentPathChanged();
        }
    }

    public Optional<Path> documentPath() {
        return Optional.ofNullable(documentPath);
    }

    public void setOnDocumentPathChanged(Consumer<Path> listener) {
        documentPathListeners.add(listener);
    }

    private void onDocumentPathChanged() {
        documentPathListeners.forEach(c -> c.accept(documentPath));
    }

    public static final double DEFAULT_ZOOM_FACTOR = 1.0;
    public static final double MAX_ZOOM = 1.5;
    public static final double MIN_ZOOM = 0.3;
    public static final double ZOOM_STEP = 0.1;

    private final BlockGroupIndex blockGroupIndex = new BlockGroupIndex();
    private final AutoConnectIndex wirelessIndex = new AutoConnectIndex();

    private final BooleanProperty savable = new SimpleBooleanProperty(this, "savable", false);
    private final ObjectProperty<File> file = new SimpleObjectProperty(this, "file", null);

    private final DoubleProperty zoomFactor = new SimpleDoubleProperty(DEFAULT_ZOOM_FACTOR);
    private final DoubleProperty translateX = new SimpleDoubleProperty(0.);
    private final DoubleProperty translateY = new SimpleDoubleProperty(0.);

    private final ObservableSet<BlockModel> blockModels = FXCollections.observableSet();
    private final ObservableSet<ConnectionModel> connectionModels = FXCollections.observableSet();
    private final ObservableSet<BlockGroupModel> blockGroupModels = FXCollections.observableSet();
    private final ObservableMap<Class<?>, List<PortModel>> dataTransmittors = FXCollections.observableHashMap();

    public AutoConnectIndex getAutoConnectIndex() {
        return wirelessIndex;
    }

    public BooleanProperty savableProperty() {
        return savable;
    }

    public ObjectProperty<File> fileProperty() {
        return file;
    }

    public DoubleProperty zoomFactorProperty() {
        return zoomFactor;
    }

    public DoubleProperty translateXProperty() {
        return translateX;
    }

    public DoubleProperty translateYProperty() {
        return translateY;
    }

    public void resetZoomFactor() {
        zoomFactor.set(DEFAULT_ZOOM_FACTOR);
    }

    // Increment zoom factor by the defined step size
    public double getIncrementedZoomFactor() {
        return Math.min(MAX_ZOOM, zoomFactor.get() + ZOOM_STEP);
    }

    // Decrement zoom factor by the defined step size
    public double getDecrementedZoomFactor() {
        return Math.max(MIN_ZOOM, zoomFactor.get() - ZOOM_STEP);
    }

    public void setZoomFactor(double factor) {
        this.zoomFactor.set(Math.round(factor * 10) / 10.);
    }

    public void reset() {
        resetZoomFactor();
        translateXProperty().set(0.);
        translateYProperty().set(0.);
        blockModels.clear();
        connectionModels.clear();
        blockGroupModels.clear();
    }

    /**
     *
     * BLOCKS
     */
    public void addBlockModel(BlockModel blockModel) {
        List<PortModel> transmittingPorts = blockModel.getTransmittingPorts();
        for (PortModel port : transmittingPorts) {
            dataTransmittors.computeIfAbsent(port.getDataType(), dataType -> new ArrayList<>()).add(port);
        }
        blockModels.add(blockModel);
        blockModel.setActive(true); // blocks and connections are first activated when added to the workspace to avoid unnecessary processing e.g. during copy & paste
    }

    public void removeBlockModel(BlockModel blockModel) {
        List<PortModel> transmittingPorts = blockModel.getTransmittingPorts();
        for (PortModel port : transmittingPorts) {
            List<PortModel> list = dataTransmittors.get(port.getDataType());
            if (list != null) {
                list.remove(port);
            }
        }
        blockModels.remove(blockModel);
        blockModel.remove();
    }

    public ObservableSet<BlockModel> getBlockModels() {
        return FXCollections.unmodifiableObservableSet(blockModels);
    }

    public void addBlockModelsListener(SetChangeListener<BlockModel> listener) {
        blockModels.addListener(listener);
    }

    public void removeBlockModelsListener(SetChangeListener<BlockModel> listener) {
        blockModels.removeListener(listener);
    }

    /**
     *
     * CONNECTIONS
     */
    public ConnectionModel addConnectionModel(PortModel startPort, PortModel endPort) {
        ConnectionModel connectionModel = new ConnectionModel(startPort, endPort);
        addConnectionModel(connectionModel);
        return connectionModel;
    }

    public void addConnectionModel(ConnectionModel connectionModel) {
        connectionModels.add(connectionModel);
        connectionModel.setActive(true); // blocks and connections are first activated when added to the workspace to avoid unnecessary processing e.g. during copy & paste
    }

    public void removeConnectionModel(ConnectionModel connectionModel) {
        connectionModels.remove(connectionModel);
        connectionModel.remove();
    }

    public ObservableSet<ConnectionModel> getConnectionModels() {
        return FXCollections.unmodifiableObservableSet(connectionModels);
    }

    public void addConnectionModelsListener(SetChangeListener<ConnectionModel> listener) {
        connectionModels.addListener(listener);
    }

    public void removeConnectionModelsListener(SetChangeListener<ConnectionModel> listener) {
        connectionModels.removeListener(listener);
    }

    public List<ConnectionModel> removeConnectionModels(BlockModel blockModel) {
        if (UiApp.LOG_METHOD_CALLS) {
            System.out.println("WorkspaceModel.removeConnectionModels()");
        }
        List<ConnectionModel> connections = blockModel.getConnections();
        for (ConnectionModel connection : connections) {
            removeConnectionModel(connection);
        }
        return connections;
    }

    /**
     *
     * GROUPS
     */
    public void removeBlockGroupModel(BlockGroupModel blockGroupModel) {
        if (UiApp.LOG_METHOD_CALLS) {
            System.out.println("WorkspaceModel.removeBlockGroupModel()");
        }
        blockGroupModels.remove(blockGroupModel);
        blockGroupModel.remove();
    }

    public ObservableSet<BlockGroupModel> getBlockGroupModels() {
        return FXCollections.unmodifiableObservableSet(blockGroupModels);
    }

    public void addBlockGroupModelsListener(SetChangeListener<BlockGroupModel> listener) {
        blockGroupModels.addListener(listener);
    }

    public void removeBlockGroupModelsListener(SetChangeListener<BlockGroupModel> listener) {
        blockGroupModels.removeListener(listener);
    }

    public void addBlockGroupModel(BlockGroupModel blockGroupModel) {
        if (UiApp.LOG_METHOD_CALLS) {
            System.out.println("WorkspaceModel.addBlockGroupModel()");
        }
        blockGroupModels.add(blockGroupModel);
    }

    public BlockGroupModel removeBlockFromGroup(BlockModel blockModel) {
        if (UiApp.LOG_METHOD_CALLS) {
            System.out.println("WorkspaceModel.removeBlockFromGroup()");
        }
        BlockGroupModel blockGroupModel = blockGroupIndex.getBlockGroup(blockModel);
        if (blockGroupModel != null) {
            blockGroupModel.removeBlock(blockModel);
            if (blockGroupModel.getBlocks().size() <= 1) {
                removeBlockGroupModel(blockGroupModel);
            }
        }
        return blockGroupModel;
    }

    public BlockGroupIndex getBlockGroupIndex() {
        return blockGroupIndex;
    }

    public BlockGroupModel getBlockGroup(BlockModel block) {
        return blockGroupIndex.getBlockGroup(block);
    }

}

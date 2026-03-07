package blocksmith.ui.workspace;

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
import blocksmith.ui.graph.group.BlockGroupModel;
import blocksmith.ui.graph.connection.ConnectionModel;
import blocksmith.ui.graph.block.BlockModel;
import blocksmith.ui.graph.port.PortModel;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import blocksmith.ui.UiApp;
import java.util.Objects;
import blocksmith.app.inbound.GraphMutationAndHistory;
import blocksmith.domain.block.Block;
import blocksmith.domain.graph.GraphDiff;
import blocksmith.domain.group.Group;
import blocksmith.domain.group.GroupId;
import blocksmith.ui.graph.block.MethodBlockNew;
import blocksmith.app.workspace.SaveDocument;
import blocksmith.Launcher;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author Joost
 */
public class WorkspaceSession {

    private final static Logger LOGGER = Logger.getLogger(WorkspaceSession.class.getName());

    private final GraphEditor editor;
    private final SaveDocument saveDocument;

    private final SelectionModel selectionModel;

    private GraphDocument document;
    private Path documentPath;
    private List<Consumer<Path>> documentPathListeners = new ArrayList<>();

    private WorkspaceSession(
            Path documentPath,
            GraphDocument document,
            GraphEditorFactory editorFactory,
            SaveDocument saveDocument
    ) {
        this.documentPath = documentPath;
        this.document = document;
        this.editor = editorFactory.createDefault(document.graph());
        this.zoomFactor.set(document.zoomFactor());
        this.translateX.set(document.translateX());
        this.translateY.set(document.translateY());
        this.saveDocument = saveDocument;

        this.selectionModel = new SelectionModel(editor);
    }

    public static WorkspaceSession newDocument(
            GraphEditorFactory editorFactory,
            SaveDocument saveDocument
    ) {
        Path path = null;
        var document = GraphDocument.createEmpty();
        return new WorkspaceSession(path, document, editorFactory, saveDocument);
    }

    public static WorkspaceSession openDocument(
            Path path,
            GraphDocument document,
            GraphEditorFactory editorFactory,
            SaveDocument saveDocument
    ) {
        return new WorkspaceSession(path, document, editorFactory, saveDocument);
    }

    public SelectionModel selectionModel() {
        return selectionModel;
    }

    public Graph graphSnapshot() {
        return editor.graphSnapshot();
    }

    public void addGraphListener(BiConsumer<Graph, Graph> listener) {
        editor.addGraphListener(listener);
    }

    public boolean isSelectionGroupable() {
        var selected = selectionModel.selected();
        if (selected.size() < Group.MINIMUM_SIZE) {
            return false;
        }
        var graph = graphSnapshot();
        for (var block : selected) {
            if (graph.groupOf(block).isPresent()) {
                return false;
            }
        }
        return true;
    }

    public GraphMutationAndHistory graphEditor() {
        return editor;
    }

    public boolean isSaved() {
        return Objects.equals(editor.graphSnapshot(), document.graph());
    }

    public void saveDocument(Path path) throws Exception {
        var graph = editor.graphSnapshot();
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

    private final ObjectProperty<File> file = new SimpleObjectProperty(this, "file", null);

    private final DoubleProperty zoomFactor = new SimpleDoubleProperty(DEFAULT_ZOOM_FACTOR);
    private final DoubleProperty translateX = new SimpleDoubleProperty(0.);
    private final DoubleProperty translateY = new SimpleDoubleProperty(0.);

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
    }

}

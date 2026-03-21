package blocksmith.ui.workspace;

import blocksmith.app.GraphDocument;
import blocksmith.app.GraphEditorFactory;
import blocksmith.app.inbound.GraphEditor;
import blocksmith.domain.graph.Graph;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import blocksmith.app.inbound.GraphMutationAndHistory;
import blocksmith.domain.group.Group;
import blocksmith.app.workspace.SaveDocument;
import blocksmith.exec.ForgeSession;
import blocksmith.exec.ForgeSessionFactory;
import blocksmith.exec.ForgeState;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 *
 * @author Joost
 */
public class WorkspaceSession {

    private final static Logger LOGGER = Logger.getLogger(WorkspaceSession.class.getName());

    private final GraphEditor editor;
    private final ForgeSession executionSession;
    private final SaveDocument saveDocument;

    private final SelectionModel selectionModel;
    private final Viewport viewport;

    private GraphDocument document;
    private Path documentPath;
    private List<Consumer<Path>> documentPathListeners = new ArrayList<>();

    private WorkspaceSession(
            Path documentPath,
            GraphDocument document,
            GraphEditorFactory editorFactory,
            ForgeSessionFactory executionSessionFactory,
            SaveDocument saveDocument
    ) {
        this.documentPath = documentPath;
        this.document = document;
        this.editor = editorFactory.createDefault(document.graph());
        this.executionSession = executionSessionFactory.create(document.graph());
        this.saveDocument = saveDocument;

        this.selectionModel = new SelectionModel(editor);
        this.viewport = new Viewport(document);
        
        editor.addGraphListener(executionSession::onGraphChanged);
    }

    public static WorkspaceSession newDocument(
            GraphEditorFactory editorFactory,
            ForgeSessionFactory executionSessionFactory,
            SaveDocument saveDocument
    ) {
        Path path = null;
        var document = GraphDocument.createEmpty();
        return new WorkspaceSession(path, document, editorFactory, executionSessionFactory, saveDocument);
    }

    public static WorkspaceSession openDocument(
            Path path,
            GraphDocument document,
            GraphEditorFactory editorFactory,
            ForgeSessionFactory executionSessionFactory,
            SaveDocument saveDocument
    ) {
        return new WorkspaceSession(path, document, editorFactory, executionSessionFactory, saveDocument);
    }

    public SelectionModel selectionModel() {
        return selectionModel;
    }

    public Viewport viewport() {
        return viewport;
    }
    
    public Graph graphSnapshot() {
        return editor.graphSnapshot();
    }

    public void addGraphListener(BiConsumer<Graph, Graph> listener) {
        editor.addGraphListener(listener);
    }

    public boolean isSelectionGroupable() {
        return selectionModel.isSelectionGroupable(graphSnapshot());
    }

    public GraphMutationAndHistory graphEditor() {
        return editor;
    }
    
    public ForgeState runtimeState() {
        return executionSession.runtimeState();
    }

    public boolean isSaved() {
        return Objects.equals(editor.graphSnapshot(), document.graph());
    }

    public void saveDocument(Path path) throws Exception {
        var graph = editor.graphSnapshot();
        var newVersion = new GraphDocument(graph, viewport.zoomFactorProperty().get(), viewport.translateXProperty().get(), viewport.translateYProperty().get());
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

}

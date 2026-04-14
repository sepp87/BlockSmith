package blocksmith.app.workspace;

import blocksmith.app.GraphDocument;
import blocksmith.app.GraphEditorFactory;
import blocksmith.app.inbound.GraphEditor;
import blocksmith.domain.graph.Graph;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import blocksmith.app.inbound.GraphMutationAndHistory;
import blocksmith.app.workspace.SaveDocument;
import blocksmith.exec.ExecutionSession;
import blocksmith.exec.ExecutionSessionFactory;
import blocksmith.exec.ExecutionState;
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
    private final ExecutionSession executionSession;
    private final SaveDocument saveDocument;

    private final SelectionState selection;
    private ViewportState viewport;
    private List<Consumer<ViewportState>> viewportListeners = new ArrayList<>();

    private GraphDocument document;
    private Path documentPath;
    private List<Consumer<Path>> documentPathListeners = new ArrayList<>();

    private WorkspaceSession(
            Path documentPath,
            GraphDocument document,
            GraphEditorFactory editorFactory,
            ExecutionSessionFactory executionSessionFactory,
            SaveDocument saveDocument
    ) {
        this.documentPath = documentPath;
        this.document = document;
        this.editor = editorFactory.createDefault(document.graph());
        this.executionSession = executionSessionFactory.create(document.graph());
        this.saveDocument = saveDocument;

        this.selection = new SelectionState(editor);
        this.viewport = ViewportState.of(document);

        editor.addGraphListener(executionSession::onGraphChanged);
    }

    public static WorkspaceSession newDocument(
            GraphEditorFactory editorFactory,
            ExecutionSessionFactory executionSessionFactory,
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
            ExecutionSessionFactory executionSessionFactory,
            SaveDocument saveDocument
    ) {
        return new WorkspaceSession(path, document, editorFactory, executionSessionFactory, saveDocument);
    }

    public SelectionState selection() {
        return selection;
    }

    public void updateViewport(ViewportState update) {
        viewport = update;
        onViewportChanged();
    }
    
    public ViewportState viewport() {
        return viewport;
    }

    public void addViewportListener(Consumer<ViewportState> listener) {
        viewportListeners.add(listener);
    }

    private void onViewportChanged() {
        viewportListeners.forEach(c -> c.accept(viewport));
    }

    public Graph graphSnapshot() {
        return editor.graphSnapshot();
    }

    public void addGraphListener(BiConsumer<Graph, Graph> listener) {
        editor.addGraphListener(listener);
    }

    public boolean isSelectionGroupable() {
        return selection.isSelectionGroupable(graphSnapshot());
    }

    public GraphMutationAndHistory graphEditor() {
        return editor;
    }

    public ExecutionState runtimeState() {
        return executionSession.runtimeState();
    }

    public boolean isSaved() {
        return Objects.equals(editor.graphSnapshot(), document.graph());
    }

    public void saveDocument(Path path) throws Exception {
        var graph = editor.graphSnapshot();
        var newVersion = new GraphDocument(graph, viewport.zoomFactor(), viewport.translateX(), viewport.translateY());
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

    
    public void start() {
        executionSession.start();
    }
}

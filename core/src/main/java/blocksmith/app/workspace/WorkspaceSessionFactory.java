package blocksmith.app.workspace;

import blocksmith.app.GraphEditorFactory;
import blocksmith.app.outbound.GraphRepo;
import blocksmith.exec.ExecutionSessionFactory;
import java.nio.file.Path;

/**
 *
 * @author joost
 */
public class WorkspaceSessionFactory {

    private final GraphRepo repo;
    private final GraphEditorFactory graphEditorFactory;
    private final TypeSessionFactory typeSessionFactory;
    private final ExecutionSessionFactory executionSessionFactory;
    private final SaveDocument saveDocument;

    public WorkspaceSessionFactory(
            GraphRepo repo,
            GraphEditorFactory graphEditorFactory,
            TypeSessionFactory typeSessionFactory,
            ExecutionSessionFactory executionSessionFactory,
            SaveDocument saveDocument
    ) {
        this.repo = repo;
        this.graphEditorFactory = graphEditorFactory;
        this.typeSessionFactory = typeSessionFactory;
        this.executionSessionFactory = executionSessionFactory;
        this.saveDocument = saveDocument;

    }

    public WorkspaceSession newDocument() {
        var workspace = WorkspaceSession.newDocument(graphEditorFactory, typeSessionFactory, executionSessionFactory, saveDocument);
        return workspace;
    }

    public WorkspaceSession openDocument(Path path) throws Exception {
        var document = repo.load(path);
        var workspace = WorkspaceSession.openDocument(path, document, graphEditorFactory, typeSessionFactory, executionSessionFactory, saveDocument);
        return workspace;
    }
}

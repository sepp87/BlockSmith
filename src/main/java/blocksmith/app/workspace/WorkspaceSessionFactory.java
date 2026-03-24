package blocksmith.app.workspace;

import blocksmith.app.GraphEditorFactory;
import blocksmith.app.outbound.GraphRepo;
import blocksmith.exec.ExecutionSessionFactory;
import blocksmith.ui.workspace.WorkspaceSession;
import java.nio.file.Path;

/**
 *
 * @author joost
 */
public class WorkspaceSessionFactory {

    private final GraphRepo repo;
    private final GraphEditorFactory graphEditorFactory;
    private final ExecutionSessionFactory executionSessionFactory;
    private final SaveDocument saveDocument;

    public WorkspaceSessionFactory(
            GraphRepo repo,
            GraphEditorFactory graphEditorFactory,
            ExecutionSessionFactory executionSessionFactory,
            SaveDocument saveDocument
    ) {
        this.repo = repo;
        this.graphEditorFactory = graphEditorFactory;
        this.executionSessionFactory = executionSessionFactory;
        this.saveDocument = saveDocument;

    }

    public WorkspaceSession newDocument() {
        var workspace = WorkspaceSession.newDocument(graphEditorFactory, executionSessionFactory, saveDocument);
        return workspace;
    }

    public WorkspaceSession openDocument(Path path) throws Exception {
        var document = repo.load(path);
        var workspace = WorkspaceSession.openDocument(path, document, graphEditorFactory, executionSessionFactory, saveDocument);
        return workspace;
    }
}

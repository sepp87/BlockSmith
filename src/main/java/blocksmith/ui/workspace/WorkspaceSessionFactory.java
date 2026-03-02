package blocksmith.ui.workspace;

import blocksmith.app.GraphEditorFactory;
import blocksmith.app.outbound.GraphRepo;
import blocksmith.ui.BlockModelFactory;
import btscore.workspace.WorkspaceSession;
import java.nio.file.Path;

/**
 *
 * @author joost
 */
public class WorkspaceSessionFactory {

    private final GraphRepo repo;
    private final GraphEditorFactory graphEditorFactory;
    private final BlockModelFactory blockFactory;
    private final SaveDocument saveDocument;

    public WorkspaceSessionFactory(
            GraphRepo repo,
            GraphEditorFactory graphEditorFactory,
            BlockModelFactory blockFactory,
            SaveDocument saveDocument
            
    ) {
        this.repo = repo;
        this.graphEditorFactory = graphEditorFactory;
        this.blockFactory = blockFactory;
        this.saveDocument = saveDocument;
        
    }

    public WorkspaceSession newDocument() {
        var workspace = WorkspaceSession.newDocument(graphEditorFactory, blockFactory, saveDocument);
        return workspace;
    }

    public WorkspaceSession openDocument(Path path) throws Exception {
        var document = repo.load(path);
        var workspace = WorkspaceSession.openDocument(path, document, graphEditorFactory, blockFactory, saveDocument);
        return workspace;
    }
}

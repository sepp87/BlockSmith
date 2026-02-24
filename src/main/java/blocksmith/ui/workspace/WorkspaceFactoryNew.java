package blocksmith.ui.workspace;

import blocksmith.app.GraphEditorFactory;
import blocksmith.app.outbound.GraphRepo;
import blocksmith.ui.BlockModelFactory;
import btscore.workspace.WorkspaceModel;
import java.nio.file.Path;

/**
 *
 * @author joost
 */
public class WorkspaceFactoryNew {

    private final GraphRepo repo;
    private final GraphEditorFactory graphEditorFactory;
    private final BlockModelFactory blockFactory;
    private final SaveDocument saveDocument;

    public WorkspaceFactoryNew(
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

    public WorkspaceModel newDocument() {
        var workspace = WorkspaceModel.newDocument(graphEditorFactory, blockFactory, saveDocument);
        return workspace;
    }

    public WorkspaceModel openDocument(Path path) throws Exception {
        var document = repo.load(path);
        var workspace = WorkspaceModel.openDocument(path, document, graphEditorFactory, blockFactory, saveDocument);
        return workspace;
    }
}

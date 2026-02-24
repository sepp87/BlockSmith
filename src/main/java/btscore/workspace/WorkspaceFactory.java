package btscore.workspace;

import blocksmith.app.GraphDocument;
import blocksmith.app.GraphEditorFactory;
import blocksmith.domain.graph.Graph;
import blocksmith.ui.BlockModelFactory;
import blocksmith.ui.workspace.SaveDocument;
import btscore.editor.context.ActionManager;
import btscore.editor.context.CommandFactory;
import java.nio.file.Path;

/**
 *
 * @author joost
 */
public class WorkspaceFactory {

    private final GraphEditorFactory graphEditorFactory;
    private final ActionManager actionManager;
    private final CommandFactory commandFactory;
    private final BlockModelFactory blockFactory;
    private final SaveDocument saveDocument;

    public WorkspaceFactory(
            GraphEditorFactory graphEditorFactory,
            ActionManager actionManager,
            CommandFactory commandFactory,
            BlockModelFactory blockFactory,
            SaveDocument saveDocument
            
    ) {
        this.graphEditorFactory = graphEditorFactory;
        this.actionManager = actionManager;
        this.commandFactory = commandFactory;
        this.blockFactory = blockFactory;
        this.saveDocument = saveDocument;
    }

    public WorkspaceContext newDocument() {
        var state = new WorkspaceState();
        var view = new WorkspaceView();
        var model = WorkspaceModel.newDocument(graphEditorFactory, blockFactory, saveDocument);
        var context = new WorkspaceContext(state);
        var controller = new WorkspaceController(actionManager, commandFactory, context, model, view);
        context.attachController(controller);
        return context;
    }

    public WorkspaceContext openDocument(Path path, GraphDocument document) {
        var state = new WorkspaceState();
        var view = new WorkspaceView();
        var model = WorkspaceModel.openDocument(path, document, graphEditorFactory, blockFactory, saveDocument);
        var context = new WorkspaceContext(state);
        var controller = new WorkspaceController(actionManager, commandFactory, context, model, view);
        context.attachController(controller);
        return context;
    }

}

package btscore.workspace;

import blocksmith.app.DefaultGraphEditor;
import blocksmith.app.GraphEditorFactory;
import blocksmith.domain.graph.Graph;
import blocksmith.ui.BlockModelFactory;
import btscore.editor.context.ActionManager;
import btscore.editor.context.CommandFactory;

/**
 *
 * @author joost
 */
public class WorkspaceFactory {

    private final GraphEditorFactory graphEditorFactory;
    private final ActionManager actionManager;
    private final CommandFactory commandFactory;
    private final BlockModelFactory blockFactory;

    public WorkspaceFactory(GraphEditorFactory graphEditorFactory, ActionManager actionManager, CommandFactory commandFactory, BlockModelFactory blockFactory) {
        this.graphEditorFactory = graphEditorFactory;
        this.actionManager = actionManager;
        this.commandFactory = commandFactory;
        this.blockFactory = blockFactory;
    }

    public WorkspaceContext create(Graph graph) {
        var editor = graphEditorFactory.createDefault(graph);
        var state = new WorkspaceState();
        var history = WorkspaceHistory.create();
        var view = new WorkspaceView();
        var model = new WorkspaceModel(editor, blockFactory);
        var context = new WorkspaceContext(state, history);
        var controller = new WorkspaceController(actionManager, commandFactory, context, model, view);
        context.attachController(controller);
        return context;
    }

}



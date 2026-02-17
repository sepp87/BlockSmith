package btscore.workspace;

import blocksmith.app.DefaultGraphEditor;
import blocksmith.app.GraphEditorFactory;
import blocksmith.domain.graph.Graph;
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

    public WorkspaceFactory(GraphEditorFactory graphEditorFactory, ActionManager actionManager, CommandFactory commandFactory) {
        this.graphEditorFactory = graphEditorFactory;
        this.actionManager = actionManager;
        this.commandFactory = commandFactory;
    }

    public WorkspaceContext create(Graph graph) {
        var editor = graphEditorFactory.createDefault(graph);
        var state = new WorkspaceState();
        var history = WorkspaceHistory.create();
        var view = new WorkspaceView();
        var model = new WorkspaceModel(editor);
        var context = new WorkspaceContext(state, history);
        var controller = new WorkspaceController(actionManager, commandFactory, context, model, view);
        context.attachController(controller);
        return context;
    }

}



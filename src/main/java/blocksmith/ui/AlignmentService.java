package blocksmith.ui;

import blocksmith.app.inbound.GraphEditor;
import btscore.UiApp;
import btscore.workspace.GraphProjection;
import btscore.workspace.SelectionModel;
import btscore.workspace.WorkspaceSession;

/**
 *
 * @author joost
 */
public class AlignmentService {

    private final SelectionModel selection;
    private final WorkspaceSession graph;
    private final GraphProjection projection;
    private final GraphEditor editor;

//        public AlignmentService(SelectionModel selection, GraphProjection graph, GraphEditor editor) {
    public AlignmentService(
            SelectionModel selection,
            WorkspaceSession graph, 
            GraphProjection projection,
            GraphEditor editor
    ) {
        this.selection = selection;
        this.graph = graph;
        this.projection = projection;
        this.editor = editor;
    }

    public void align(AlignmentPolicy.Mode mode) {
        var align = new AlignmentPolicy();
        var ids = selection.selected();
        var blocks = UiApp.SWITCH_PROJECTION ? projection.blocks(ids) : graph.blocks(ids);
        var positions = align.apply(blocks, mode);
        editor.moveBlocks(positions);
    }

}

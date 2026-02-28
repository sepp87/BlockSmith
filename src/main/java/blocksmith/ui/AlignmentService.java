package blocksmith.ui;

import blocksmith.app.inbound.GraphEditor;
import btscore.workspace.GraphProjection;
import btscore.workspace.SelectionModel;
import btscore.workspace.WorkspaceModel;

/**
 *
 * @author joost
 */
public class AlignmentService {

    private final SelectionModel selection;
    private final WorkspaceModel graph;
    private final GraphEditor editor;

//        public AlignmentService(SelectionModel selection, GraphProjection graph, GraphEditor editor) {
    public AlignmentService(SelectionModel selection, WorkspaceModel graph, GraphEditor editor) {
        this.selection = selection;
        this.graph = graph;
        this.editor = editor;
    }

    public void align(AlignmentPolicy.Mode mode) {
        var align = new AlignmentPolicy();
        var ids = selection.selected();
        var blocks = graph.blocks(ids);
        var positions = align.apply(blocks, mode);
        editor.moveBlocks(positions);
    }

}

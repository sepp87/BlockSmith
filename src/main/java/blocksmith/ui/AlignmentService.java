package blocksmith.ui;

import blocksmith.app.inbound.GraphEditor;
import blocksmith.app.inbound.GraphMutationAndHistory;
import blocksmith.ui.projection.GraphProjection;
import blocksmith.ui.workspace.SelectionModel;

/**
 *
 * @author joost
 */
public class AlignmentService {

    private final SelectionModel selection;
    private final GraphProjection projection;
    private final GraphMutationAndHistory editor;

    public AlignmentService(
            SelectionModel selection,
            GraphProjection projection,
            GraphMutationAndHistory editor
    ) {
        this.selection = selection;
        this.projection = projection;
        this.editor = editor;
    }

    public void apply(AlignmentPolicy.Mode mode) {
        var align = new AlignmentPolicy();
        var ids = selection.selected();
        var blocks =  projection.blocks(ids);
        var positions = align.apply(blocks, mode);
        editor.moveBlocks(positions);
    }

}

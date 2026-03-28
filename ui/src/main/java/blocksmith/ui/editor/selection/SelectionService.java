package blocksmith.ui.editor.selection;

import blocksmith.app.workspace.SelectionState;
import blocksmith.ui.projection.GraphProjection;
import blocksmith.domain.block.BlockId;
import java.util.ArrayList;
import javafx.geometry.Bounds;

/**
 *
 * @author joost
 */
public class SelectionService {

    private final SelectionState selection;
    private final GraphProjection projection;

    public SelectionService(
            SelectionState selection,
            GraphProjection projection
    ) {
        this.selection = selection;
        this.projection = projection;
    }

    public void rectangleSelect(Bounds rect) {
        var blocks = projection.blocks();
        var toSelect = new ArrayList<BlockId>();
        for (var block : blocks) {
            if (rect.contains(block.measuredBounds())) {
                toSelect.add(BlockId.from(block.getId()));
            }
        }
        selection.select(toSelect);
    }
}

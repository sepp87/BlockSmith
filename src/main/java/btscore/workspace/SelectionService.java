package btscore.workspace;

import blocksmith.domain.block.BlockId;
import btscore.graph.block.BlockController;
import btscore.graph.block.BlockModel;
import btscore.graph.block.BlockView;
import java.util.ArrayList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;

/**
 *
 * @author joost
 */
public class SelectionService {

    private final SelectionModel selection;
    private final WorkspaceModel graph;

    public SelectionService(SelectionModel selection, WorkspaceModel graph) {
        this.selection = selection;
        this.graph = graph;
    }

    public void rectangleSelect(Bounds rect) {
        var blocks = graph.blocks();
        var toSelect = new ArrayList<BlockId>();
        for (var block : blocks) {
            if(rect.contains(block.measuredBounds())){
                toSelect.add(BlockId.from(block.getId()));
            }
        }
        selection.select(toSelect);
    }
}

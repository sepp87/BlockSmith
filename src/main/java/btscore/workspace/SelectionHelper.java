package btscore.workspace;

import blocksmith.domain.block.BlockId;
import btscore.graph.block.BlockController;
import btscore.graph.block.BlockView;
import btscore.graph.block.BlockModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javafx.geometry.Point2D;

/**
 *
 * @author Joost
 */
public class SelectionHelper {

    private final WorkspaceModel model;
    private final WorkspaceController controller;

    public SelectionHelper(WorkspaceModel model, WorkspaceController controller) {
        this.model = model;
        this.controller = controller;
    }

    public void rectangleSelect(Point2D selectionMin, Point2D selectionMax) {
        var selectedBlocks = new ArrayList<BlockController>();
        for (BlockModel block : model.getBlockModels()) {
            BlockController blockController = controller.getBlockController(block);
            BlockView blockView = blockController.getView();
            if (true // unnecessary statement for readability
                    && block.layoutXProperty().get() >= selectionMin.getX()
                    && block.layoutXProperty().get() + blockView.getWidth() <= selectionMax.getX()
                    && block.layoutYProperty().get() >= selectionMin.getY()
                    && block.layoutYProperty().get() + blockView.getHeight() <= selectionMax.getY()) {

                selectedBlocks.add(blockController);

            } else {
                selectedBlocks.remove(blockController);
            }
        }
        var ids = selectedBlocks.stream().map(c -> BlockId.from(c.getModel().getId())).toList();
        model.selectionModel().select(ids);
    }

    public void updateSelection(BlockController block, boolean isModifierDown) {
        var id = BlockId.from(block.getModel().getId());
        if (isModifierDown) {
            model.selectionModel().toggle(id);
        } else {
            model.selectionModel().select(List.of(id));
        }
    }

    public void selectBlocks(Collection<BlockController> blocks) {
        var ids = blocks.stream().map(b -> BlockId.from(b.getModel().getId())).toList();
        model.selectionModel().select(ids);
    }

    public Collection<BlockController> getSelectedBlockControllers() {
        var selectedBlocks = model.selectionModel().selected().stream().map(id -> controller.getBlockController(id)).toList();
        return List.copyOf(selectedBlocks);
    }
}

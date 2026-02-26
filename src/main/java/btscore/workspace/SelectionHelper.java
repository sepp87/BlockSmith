package btscore.workspace;

import blocksmith.domain.block.BlockId;
import btscore.graph.block.BlockController;
import btscore.graph.block.BlockView;
import btscore.graph.block.BlockModel;
import java.util.Collection;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.geometry.Point2D;

/**
 *
 * @author Joost
 */
public class SelectionHelper {

    private final WorkspaceModel model;
    private final WorkspaceView view;
    private final WorkspaceController controller;

    private final ObservableSet<BlockController> selectedBlocks = FXCollections.observableSet();

    public SelectionHelper(WorkspaceModel model, WorkspaceView view, WorkspaceController controller) {
        this.model = model;
        this.view = view;
        this.controller = controller;
    }

    public void selectAllBlocks() {

        this.selectedBlocks.clear();
        for (BlockController block : controller.getBlockControllers()) {
            block.selectedProperty().set(true);
            selectedBlocks.add(block);
        }

        var ids = selectedBlocks.stream().map(c -> BlockId.from(c.getModel().getId())).toList();
        model.selectionModel().setSelected(ids);

    }

    public void deselectAllBlocks() {

        for (BlockController block : selectedBlocks) {
            block.selectedProperty().set(false);
        }
        this.selectedBlocks.clear();

        model.selectionModel().setSelected(List.of());
    }

    public void rectangleSelect(Point2D selectionMin, Point2D selectionMax) {
        for (BlockModel block : model.getBlockModels()) {
            BlockController blockController = controller.getBlockController(block);
            BlockView blockView = blockController.getView();
            if (true // unnecessary statement for readability
                    && block.layoutXProperty().get() >= selectionMin.getX()
                    && block.layoutXProperty().get() + blockView.getWidth() <= selectionMax.getX()
                    && block.layoutYProperty().get() >= selectionMin.getY()
                    && block.layoutYProperty().get() + blockView.getHeight() <= selectionMax.getY()) {

                selectedBlocks.add(blockController);
                blockController.selectedProperty().set(true);

            } else {
                selectedBlocks.remove(blockController);
                blockController.selectedProperty().set(false);
            }
        }
        var ids = selectedBlocks.stream().map(c -> BlockId.from(c.getModel().getId())).toList();
        model.selectionModel().setSelected(ids);
    }

    public void updateSelection(BlockController block, boolean isModifierDown) {
        if (selectedBlocks.contains(block)) {
            if (isModifierDown) {
                // Remove this node from selection
                deselectBlock(block);
            } else {
                // Subscribe multiselection to MouseMove event
                for (BlockController selectedBlock : selectedBlocks) {
//                    selectedBlock.prepareMove();
                }
            }
        } else {
            if (isModifierDown) {
                // add this node to selection
                selectBlock(block);
            } else {
                // Deselect all blocks that are selected and select only this block
                deselectAllBlocks();
                selectBlock(block);
//                block.prepareMove();
            }
        }
        var ids = selectedBlocks.stream().map(c -> BlockId.from(c.getModel().getId())).toList();
        model.selectionModel().setSelected(ids);
    }

    public void selectBlock(BlockController block) {
        block.selectedProperty().set(true);
        selectedBlocks.add(block);

        var ids = selectedBlocks.stream().map(c -> BlockId.from(c.getModel().getId())).toList();
        model.selectionModel().setSelected(ids);
    }

    public void deselectBlock(BlockController block) {
        block.selectedProperty().set(false);
        selectedBlocks.remove(block);

        var ids = selectedBlocks.stream().map(c -> BlockId.from(c.getModel().getId())).toList();
        model.selectionModel().setSelected(ids);
    }

    public Collection<BlockController> getSelectedBlockControllers() {
        return List.copyOf(selectedBlocks);
    }
}

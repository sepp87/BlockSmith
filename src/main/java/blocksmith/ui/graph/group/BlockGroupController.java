package blocksmith.ui.graph.group;

import blocksmith.domain.block.BlockId;
import blocksmith.domain.group.GroupId;
import java.util.Collection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.collections.SetChangeListener;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import blocksmith.ui.graph.BaseController;
import blocksmith.ui.command.WorkspaceCommandBus;
import blocksmith.ui.graph.block.BlockController;
import blocksmith.ui.graph.block.BlockModel;
import blocksmith.ui.graph.block.BlockView;
import blocksmith.ui.workspace.FxWorkspaceHandle;
import blocksmith.ui.workspace.WorkspaceController;
import blocksmith.ui.workspace.WorkspaceSession;
import blocksmith.ui.workspace.WorkspaceState;

/**
 *
 * @author JoostMeulenkamp
 */
public class BlockGroupController extends BaseController {

    private final WorkspaceState state;
    private final WorkspaceController workspaceController;

    private final BlockGroupModel model;
    private final BlockGroupView view;

    private final ObservableMap<BlockModel, BlockController> children;

    public BlockGroupController(WorkspaceCommandBus commands, WorkspaceSession session, WorkspaceState state, WorkspaceController workspaceController, BlockGroupModel blockGroupModel, BlockGroupView blockGroupView) {
        super(commands, session);
        this.state = state;
        this.workspaceController = workspaceController;
        this.model = blockGroupModel;
        this.view = blockGroupView;
        this.children = FXCollections.observableHashMap();
        
        // Handlers
        view.setOnMouseEntered(this::handleMouseEntered);
        view.setOnMouseExited(this::handleMouseExited);
        view.setOnMousePressed(this::handleGroupPressed);
        view.setOnMouseReleased(this::handleGroupReleased);
        view.getBinButton().setOnAction(this::handleBinButtonClicked);

        // Listeners
        model.getBlocks().addListener(blocksListener);

        // Bindings
        view.getLabel().textProperty().bindBidirectional(model.labelProperty());
    }

    public void setBlocks(Collection<BlockController> blocks) {
        for (BlockController blockController : blocks) {
            children.put(blockController.getModel(), blockController);
            addListeners(blockController);
        }
        calculateSize();
    }

    public void dispose() {
        // Handlers
        view.setOnMouseEntered(null);
        view.setOnMouseExited(null);
        view.setOnMousePressed(null);
        view.setOnMouseReleased(null);
        view.getBinButton().setOnAction(null);

        // Listeners
        model.getBlocks().removeListener(blocksListener);

        // Bindings
        view.getLabel().textProperty().unbindBidirectional(model.labelProperty());

        for (BlockController blockController : children.values()) {
            removeListeners(blockController);
        }
    }

    public BlockGroupView getView() {
        return view;
    }

    private final SetChangeListener<BlockModel> blocksListener = this::onBlocksChanged;

    private void onBlocksChanged(SetChangeListener.Change<? extends BlockModel> change) {
        System.out.println("BlockGroupController.onBlocksChanged()");
        if (change.wasAdded()) {
            var blockModel = change.getElementAdded();
            var id = BlockId.from(blockModel.getId());
            var blockController = workspaceController.getBlockController(id);
            addListeners(blockController);
            children.put(blockModel, blockController);
        } else {
            var blockModel = change.getElementRemoved();
            BlockController blockController = children.get(blockModel);
            removeListeners(blockController);
            children.remove(blockModel);
        }
        calculateSize();
    }

    private void handleBinButtonClicked(ActionEvent event) {
        var id = GroupId.from(model.getId());
        var command = commands.factory().createRemoveGroupCommand(id);
        commands.execute(command);
    }

    private void handleMouseEntered(MouseEvent event) {
        view.getLabel().setVisible(true);
        view.getBinButton().setVisible(true);
    }

    private void handleMouseExited(MouseEvent event) {
        view.getLabel().setVisible(false);
        view.getBinButton().setVisible(false);
    }

    private void handleGroupPressed(MouseEvent event) {
        var blocks = children.values().stream().map(b -> BlockId.from(b.getModel().getId())).toList();
        session.selectionModel().select(blocks);

        for (BlockController blockController : children.values()) {
            blockController.startPoint = new Point2D(event.getSceneX(), event.getSceneY());
        }
        state.setSelectingBlockGroup(); // prevent group from being deselected
    }

    private void handleGroupReleased(MouseEvent event) {
        state.setIdle();
//        event.consume();
    }

    private void addListeners(BlockController blockController) {
        BlockView blockView = blockController.getView();
        blockView.layoutXProperty().addListener(blockTransformedListener);
        blockView.layoutYProperty().addListener(blockTransformedListener);
        blockView.widthProperty().addListener(blockTransformedListener);
        blockView.heightProperty().addListener(blockTransformedListener);
    }

    private void removeListeners(BlockController blockController) {
        BlockView blockView = blockController.getView();
        blockView.layoutXProperty().removeListener(blockTransformedListener);
        blockView.layoutYProperty().removeListener(blockTransformedListener);
        blockView.widthProperty().removeListener(blockTransformedListener);
        blockView.heightProperty().removeListener(blockTransformedListener);
    }

    private final ChangeListener<Object> blockTransformedListener = this::onBlockTransformed; // is this listening to transforms e.g. move and resize? otherwise groupBlockTransformedListener

    private void onBlockTransformed(ObservableValue b, Object o, Object n) {
        // TODO optimize here so only the changed block model is used the re-calculate the size
        calculateSize();
    }

    private void calculateSize() {
        if (children.isEmpty()) {
            return;
        }

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;

        for (BlockController blockController : children.values()) {
            BlockView blockView = blockController.getView();

            if (blockView.layoutXProperty().get() < minX) {
                minX = blockView.layoutXProperty().get();
            }
            if (blockView.layoutYProperty().get() < minY) {
                minY = blockView.layoutYProperty().get();
            }
            if ((blockView.layoutXProperty().get() + blockView.widthProperty().get()) > maxX) {
                maxX = blockView.layoutXProperty().get() + blockView.widthProperty().get();
            }
            if ((blockView.layoutYProperty().get() + blockView.heightProperty().get()) > maxY) {
                maxY = blockView.layoutYProperty().get() + blockView.heightProperty().get();
            }

//            System.out.println("x:" + blockView.layoutXProperty().get() + " y:" + blockView.layoutYProperty().get() + " w" + blockView.widthProperty().get() + " h" + blockView.heightProperty().get());
        }

        view.setPrefSize(maxX - minX, maxY - minY);
        view.relocate(minX, minY);
    }

}

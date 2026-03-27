package blocksmith.ui.graph.block;

import blocksmith.domain.block.BlockId;
import blocksmith.domain.value.Port;
import blocksmith.exec.BlockException;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import blocksmith.ui.UiApp;
import blocksmith.ui.graph.BaseController;
import blocksmith.app.workspace.WorkspaceCommandBus;
import blocksmith.ui.graph.port.PortController;
import blocksmith.ui.graph.port.PortModel;
import blocksmith.ui.graph.port.PortView;
import blocksmith.ui.utils.EventUtils;
import blocksmith.ui.projection.GraphProjection;
import blocksmith.ui.workspace.WorkspaceController;
import blocksmith.app.workspace.WorkspaceSession;
import java.util.function.Consumer;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;

/**
 *
 * @author Joost
 */
public class BlockController extends BaseController {

    private final GraphProjection projection;
    private final WorkspaceController workspaceController;
    private final BlockModel model;
    private final BlockView view;

    private final ObservableMap<PortModel, PortController> ports = FXCollections.observableHashMap();

    private final BooleanProperty selected = new SimpleBooleanProperty(false);
    private boolean infoShown = false;
    private boolean exceptionShown = false;

    public Point2D startPoint;
    private Point2D updatedPoint;

    private double previousWidth = -1;
    private double previousHeight = -1;

    public BlockController(WorkspaceCommandBus commands, WorkspaceSession session, GraphProjection projection, WorkspaceController workspaceController, BlockModel blockModel, BlockView blockView) {
        super(commands, session);
        this.projection = projection;
        this.workspaceController = workspaceController;
        this.model = blockModel;
        this.view = blockView;

        this.model.getExceptions().addListener(exceptionsListener);
        showExceptionButton();

        selected.addListener(selectionListener);

        view.setOnMouseEntered(this::handleMouseEntered);
        view.setOnMouseExited(this::handleMouseExited);
        view.getContentGrid().setOnMouseEntered(model.onMouseEntered());
        view.getContentGrid().setOnMousePressed(this::handleMoveStartedAndUpdateSelection);
        view.getContentGrid().setOnDragDetected(this::handleDragDetected);
        view.getContentGrid().setOnMouseDragged(this::handleMoveUpdated);
        view.getContentGrid().setOnMouseReleased(this::handleMoveFinished);

        view.getInfoButton().setOnAction(this::handleInfoButtonClicked);
        view.getExceptionButton().setOnAction(this::handleExceptionButtonClicked);

        view.layoutXProperty().addListener(transformListener);
        view.layoutYProperty().addListener(transformListener);

        Consumer<Bounds> boundsSub = (bounds) -> model.setMeasuredBounds(new BoundingBox(view.getLayoutX(), view.getLayoutY(), view.getWidth(), view.getHeight()));
        view.boundsInParentProperty().subscribe(boundsSub);

        view.idProperty().bind(model.idProperty());
        view.layoutXProperty().bindBidirectional(model.layoutXProperty());
        view.layoutYProperty().bindBidirectional(model.layoutYProperty());
        view.getCaptionLabel().textProperty().bindBidirectional(model.labelProperty());
        model.labelProperty().addListener((b, o, n) -> {
            var command = commands.factory().createRenameBlockCommand(BlockId.from(model.getId()), n);
            commands.execute(command);
        });

        view.addControlToBlock(model.getCustomization());

        addPorts(model.getInputPorts(), Port.Direction.INPUT);
        addPorts(model.getOutputPorts(), Port.Direction.OUTPUT);

        if (model.resizableProperty().get()) {
            view.getContentGrid().prefWidthProperty().bind(model.widthProperty());
            view.getContentGrid().prefHeightProperty().bind(model.heightProperty());
            ResizeButton resizeButton = view.getResizeButton();
            resizeButton.setOnMousePressed(this::handleResizeStarted);
            resizeButton.setOnMouseDragged(this::handleResizeUpdated);
            resizeButton.setOnMouseReleased(this::handleResizeFinished);
        }

        if (model instanceof MethodBlockNew methodBlock) {
            var inputControls = methodBlock.getInputControls();
            for (var entry : inputControls.entrySet()) {
                var valueId = entry.getKey();
                var control = entry.getValue();
                control.setOnValueChangedByUser(value -> {
                    var command = commands.factory().createUpdateParamValueCommand(BlockId.from(methodBlock.getId()), valueId, value);
                    commands.execute(command);
                });

            }
        }

        model.inputPorts.forEach(p -> p.activeProperty().addListener(n -> {

            System.out.println("TEST" + model.inputPorts.stream().noneMatch(PortModel::isActive));
            if (model.inputPorts.stream().noneMatch(PortModel::isActive)) {
                model.getExceptions().clear();
            }
        }));
    }

    private void addPorts(List<PortModel> portModels, Port.Direction direction) {
        List<PortView> portViews = new ArrayList<>();
        for (PortModel portModel : portModels) {
            PortView portView = new PortView(direction);
            portView.boundsInParentProperty().addListener(transformListener);
            portViews.add(portView);
            PortController portController = new PortController(this, portModel, portView);
            ports.put(portModel, portController);
            workspaceController.registerPort(portController);
        }
        if (direction == Port.Direction.INPUT) {
            view.addInputPorts(portViews);
        } else {
            view.addOutputPorts(portViews);
        }
    }

    private final ChangeListener<Object> transformListener = this::onTransformCalculatePortCenter;

    private void onTransformCalculatePortCenter(Object b, Object o, Object n) {
        for (PortController portController : ports.values()) {
            PortView portView = portController.getView();
            Point2D centerInScene = portView.localToScene(portView.getWidth() / 2, portView.getHeight() / 2);
            Point2D centerInLocal = workspaceController.getView().sceneToLocal(centerInScene);
            portView.centerXProperty().set(centerInLocal.getX());
            portView.centerYProperty().set(centerInLocal.getY());
        }
    }

    public void initiateConnection(PortController portController) {
        workspaceController.initiateConnection(portController);
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    ChangeListener<Boolean> selectionListener = this::onSelectionChanged;

    private void onSelectionChanged(Object b, Boolean o, Boolean n) {
        view.setSelected(n);
    }

    private void handleMouseEntered(MouseEvent event) {
        view.getCaptionLabel().setVisible(true);

        if (!infoShown) {
            view.getInfoButton().setVisible(true);
        }

        if (model.resizableProperty().get()) {
            view.getResizeButton().setVisible(true);
        }
    }

    private void handleMouseExited(MouseEvent event) {
        view.getCaptionLabel().setVisible(false);
        view.getInfoButton().setVisible(false);

        if (model.resizableProperty().get()) {
            view.getResizeButton().setVisible(false);
        }
    }

    private boolean drag_to_move = false;

    private void handleMoveStartedAndUpdateSelection(MouseEvent event) {

        drag_to_move = false;
        view.toFront();
        startPoint = new Point2D(event.getSceneX(), event.getSceneY());
        updatedPoint = startPoint;
        event.consume();

    }

    private void handleDragDetected(MouseEvent event) {
        var drag_to_duplicate = EventUtils.isModifierDown(event);

        if (drag_to_duplicate) {

        } else {
            drag_to_move = true;
            var isSelected = this.selected.get();
            if (!isSelected) {
                var id = BlockId.from(model.getId());
                var command = commands.factory().createUpdateSelectionCommand(id, false);
                commands.execute(command);
            }
        }
        event.consume();
    }

    public void handleMoveUpdated(MouseEvent event) {

        if (!drag_to_move) {
            return;
        }

        double scale = session.viewport().zoomFactor();
        double deltaX = (event.getSceneX() - updatedPoint.getX()) / scale;
        double deltaY = (event.getSceneY() - updatedPoint.getY()) / scale;

        var ids = session.selection().selected();
        var blocks = projection.blocks(ids);

        for (var blockModel : blocks) {
            double x = blockModel.layoutXProperty().get();
            double y = blockModel.layoutYProperty().get();
            blockModel.layoutXProperty().set(x + deltaX);
            blockModel.layoutYProperty().set(y + deltaY);
        }
        updatedPoint = new Point2D(event.getSceneX(), event.getSceneY());

        event.consume();

    }

    public void handleMoveFinished(MouseEvent event) {
        try {
            if (drag_to_move) {
                var ids = session.selection().selected();
                Point2D delta = updatedPoint.subtract(startPoint);
                double dX = delta.getX() / session.viewport().zoomFactor();
                double dY = delta.getY() / session.viewport().zoomFactor();
                delta = new Point2D(dX, dY);

                var command = commands.factory().createMoveBlocksCommand(ids, delta.getX(), delta.getY());
                commands.execute(command);

            } else {
                var id = BlockId.from(model.getId());
                var command = commands.factory().createUpdateSelectionCommand(id, EventUtils.isModifierDown(event));
                commands.execute(command);
            }
        } finally {
            drag_to_move = false;
        }

        event.consume();
    }

    private final ListChangeListener<BlockException> exceptionsListener = this::onExceptionsChanged;

    private void onExceptionsChanged(Change<? extends BlockException> change) {
        if (UiApp.LOG_METHOD_CALLS) {
            System.out.println("BlockController.onExceptionsChanged()");
        }
        showExceptionButton();
    }

    private void showExceptionButton() {
        if (UiApp.LOG_METHOD_CALLS) {
            System.out.println("BlockController.showExceptionButton() exceptionShown " + exceptionShown);
        }

        if (exceptionShown) {
            return;
        }
        if (this.model.getExceptions().isEmpty()) {
            view.getExceptionButton().setVisible(false);
        } else {
            view.getExceptionButton().setVisible(true);
        }
    }

    public void onInfoPanelRemoved() {
        infoShown = false;
    }

    public void onExceptionPanelRemoved() {
        exceptionShown = false;
        showExceptionButton();
    }

    private void handleInfoButtonClicked(ActionEvent event) {
        workspaceController.showInfoPanel(this);
        view.getInfoButton().setVisible(false);
        infoShown = true;
    }

    private void handleExceptionButtonClicked(ActionEvent event) {
        workspaceController.showExceptionPanel(this);
        view.getExceptionButton().setVisible(false);
        exceptionShown = true;
    }

    private void handleResizeStarted(MouseEvent event) {
        startPoint = new Point2D(event.getSceneX(), event.getSceneY());
        updatedPoint = startPoint;
        GridPane contentGrid = view.getContentGrid();
        previousWidth = contentGrid.getWidth();
        previousHeight = contentGrid.getHeight();
        model.widthProperty().set(previousWidth);
        model.heightProperty().set(previousHeight);
        event.consume();
    }

    private void handleResizeUpdated(MouseEvent event) {
        double scale = session.viewport().zoomFactor();
        double deltaX = (event.getSceneX() - updatedPoint.getX()) / scale;
        double deltaY = (event.getSceneY() - updatedPoint.getY()) / scale;
        double newWidth = model.widthProperty().get() + deltaX;
        double newHeight = model.heightProperty().get() + deltaY;
        model.widthProperty().set(newWidth);
        model.heightProperty().set(newHeight);
        updatedPoint = new Point2D(event.getSceneX(), event.getSceneY());
        event.consume();
    }

    private void handleResizeFinished(MouseEvent event) {
        if (!event.isDragDetect()) {
            double newWidth = model.widthProperty().get();
            double newHeight = model.heightProperty().get();
            var id = BlockId.from(model.getId());
            var command = commands.factory().createResizeBlockCommand(id, newWidth, newHeight);
            commands.execute(command);
        }
        event.consume();
    }

    public void setSize(double width, double height) {
        GridPane contentGrid = view.getContentGrid();
        contentGrid.setPrefSize(width, height);
    }

    public BlockView getView() {
        return view;
    }

    public BlockModel getModel() {
        return model;
    }

    public void dispose() {

        this.model.getExceptions().removeListener(exceptionsListener);
        selected.removeListener(selectionListener);

        view.setOnMouseEntered(null);
        view.setOnMouseExited(null);
        view.getContentGrid().setOnMouseEntered(null);
        view.getContentGrid().setOnMousePressed(null);
        view.getContentGrid().setOnMouseDragged(null);
        view.getContentGrid().setOnMouseReleased(null);

        view.getInfoButton().setOnAction(null);
        view.getExceptionButton().setOnAction(null);

        view.layoutXProperty().removeListener(transformListener);
        view.layoutYProperty().removeListener(transformListener);

        view.idProperty().unbind();
        view.layoutXProperty().unbindBidirectional(model.layoutXProperty());
        view.layoutYProperty().unbindBidirectional(model.layoutYProperty());
        view.getCaptionLabel().textProperty().unbindBidirectional(model.labelProperty());
        view.getCaptionLabel().remove();

        if (model.resizableProperty().get()) {
            view.getContentGrid().prefWidthProperty().unbind();
            view.getContentGrid().prefHeightProperty().unbind();
            ResizeButton resizeButton = view.getResizeButton();
            resizeButton.setOnMousePressed(null);
            resizeButton.setOnMouseDragged(null);
            resizeButton.setOnMouseReleased(null);
        }

        for (PortController portController : ports.values()) {
            portController.getView().boundsInParentProperty().removeListener(transformListener);
            workspaceController.unregisterPort(portController);
        }

    }

}

package blocksmith.ui.editor.selection;

import blocksmith.ui.UiApp;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import blocksmith.ui.command.CommandDispatcher;
import blocksmith.ui.editor.EditorEventRouter;
import blocksmith.ui.command.Command;
import blocksmith.ui.command.AppCommandFactory;
import blocksmith.ui.workspace.FxWorkspaceRegistry;
import static blocksmith.ui.utils.EditorUtils.onFreeSpace;
import javafx.geometry.BoundingBox;

/**
 *
 * @author joostmeulenkamp
 */
public class SelectionRectangleController {

    private final CommandDispatcher actionManager;
    private final AppCommandFactory commandFactory;
    private final EditorEventRouter eventRouter;
    private final FxWorkspaceRegistry context;
    private final SelectionRectangleView view;

    private Point2D startPoint;

    public SelectionRectangleController(CommandDispatcher actionManager, AppCommandFactory commandFactory, EditorEventRouter eventRouter, FxWorkspaceRegistry context, SelectionRectangleView selectionRectangleView) {
        this.actionManager = actionManager;
        this.commandFactory = commandFactory;
        this.eventRouter = eventRouter;
        this.context = context;
        this.view = selectionRectangleView;

        eventRouter.addEventListener(MouseEvent.MOUSE_PRESSED, this::handleSelectionStarted);
        eventRouter.addEventListener(MouseEvent.MOUSE_DRAGGED, this::handleSelectionUpdated);
        eventRouter.addEventListener(MouseEvent.MOUSE_RELEASED, this::handleSelectionFinished);
    }

    public void handleSelectionStarted(MouseEvent event) {
        var workspace = context.active();
        boolean onFreeSpace = onFreeSpace(event);
        boolean isPrimary = event.getButton() == MouseButton.PRIMARY;
        boolean isIdle = workspace.state().isIdle();
        if (UiApp.LOG_FREE_SPACE_CHECK) {
            System.out.println("onFreeSpace: " + onFreeSpace + "   isPrimary: " + isPrimary + "   isIdle: " + isIdle);

        }

        if (onFreeSpace && isPrimary && isIdle) {
            workspace.state().setSelecting();
            prepareSelectionRectangle(event);
        }
    }

    public void handleSelectionUpdated(MouseEvent event) {
        var workspace = context.active();
        boolean isPrimary = event.getButton() == MouseButton.PRIMARY;
        boolean isSelecting = workspace.state().isSelecting();

        if (isSelecting && isPrimary) {
            initializeSelectionRectangle();
            updateSelectionRectangle(event);
            updateSelection();

        }
    }

    public void handleSelectionFinished(MouseEvent event) {
        var workspace = context.active();

        // do NOT reset startPoint to null, because this will throw null pointer exceptions, when accidentally clicking another button when selecting
        if (event.getButton() == MouseButton.PRIMARY) {
            if (workspace.state().isSelecting()) {
                // Reset the mouse mode back to idle
                workspace.state().setIdle();
                // Check if selection rectangle is active
                if (view.isVisible()) {
                    // Finalize selection by removing the selection rectangle
                    removeSelectionRectangle();
                } else {
                    // Deselect all blocks if no selection rectangle was active
                    var command = commandFactory.createCommand(Command.Id.DESELECT_ALL_BLOCKS);
                    actionManager.executeCommand(command);
                }
            }
        }
    }

    private void prepareSelectionRectangle(MouseEvent event) {
        startPoint = view.getParent().sceneToLocal(event.getSceneX(), event.getSceneY());
    }

    private void initializeSelectionRectangle() {
        if (view.isVisible()) {
            return;
        }
        view.setVisible(true);
        view.setLayoutX(startPoint.getX());
        view.setLayoutY(startPoint.getY());
        view.setMinSize(0, 0);
    }

    private void updateSelectionRectangle(MouseEvent event) {

        Point2D currentPosition = view.getParent().sceneToLocal(event.getSceneX(), event.getSceneY());
        Point2D delta = currentPosition.subtract(startPoint);

        var x = delta.getX() < 0 ? currentPosition.getX() : view.getLayoutX();
        var y = delta.getY() < 0 ? currentPosition.getY() : view.getLayoutY();

        view.resize(Math.abs(delta.getX()), Math.abs(delta.getY()));
        view.relocate(x, y);
    }

    private void updateSelection() {

        var rectOnScene = view.getParent().localToScene(view.getBoundsInParent());
        var rectOnWorkspace = context.active().view().sceneToLocal(rectOnScene);

        var command = commandFactory.createRectangleSelectCommand(rectOnWorkspace);
        actionManager.executeCommand(command);
    }

    private void removeSelectionRectangle() {
        view.setVisible(false);
    }

}

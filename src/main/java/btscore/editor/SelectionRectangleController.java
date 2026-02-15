package btscore.editor;

import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import btscore.UiApp;
import btscore.editor.context.ActionManager;
import btscore.editor.context.EditorEventRouter;
import btscore.workspace.WorkspaceState;
import btscore.editor.context.Command;
import btscore.editor.context.EditorContext;
import static btscore.utils.EditorUtils.onFreeSpace;

/**
 *
 * @author joostmeulenkamp
 */
public class SelectionRectangleController {

    private final EditorEventRouter eventRouter;
    private final EditorContext context;
    private final SelectionRectangleView view;

    private Point2D startPoint;

    public SelectionRectangleController(EditorEventRouter eventRouter, EditorContext context, SelectionRectangleView selectionRectangleView) {
        this.eventRouter = eventRouter;
        this.context = context;
        this.view = selectionRectangleView;

        eventRouter.addEventListener(MouseEvent.MOUSE_PRESSED, this::handleSelectionStarted);
        eventRouter.addEventListener(MouseEvent.MOUSE_DRAGGED, this::handleSelectionUpdated);
        eventRouter.addEventListener(MouseEvent.MOUSE_RELEASED, this::handleSelectionFinished);
    }

    public void handleSelectionStarted(MouseEvent event) {
        var workspace = context.activeWorkspace();
        boolean onFreeSpace = onFreeSpace(event);
        boolean isPrimary = event.getButton() == MouseButton.PRIMARY;
        boolean isIdle = workspace.state().isIdle();
        if (onFreeSpace && isPrimary && isIdle) {
            workspace.state().setSelecting();
            prepareSelectionRectangle(event);
        }
    }

    public void handleSelectionUpdated(MouseEvent event) {
        var workspace = context.activeWorkspace();
        boolean isPrimary = event.getButton() == MouseButton.PRIMARY;
        boolean isSelecting = workspace.state().isSelecting();

        if (isSelecting && isPrimary) {
            initializeSelectionRectangle();
            updateSelectionRectangle(event);
            updateSelection();
        }
    }

    public void handleSelectionFinished(MouseEvent event) {
        var workspace = context.activeWorkspace();

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
                    var command = workspace.commandFactory().createCommand(Command.Id.DESELECT_ALL_BLOCKS);
                    workspace.actionManager().executeCommand(command);
                }
            }
        }
    }

    private void prepareSelectionRectangle(MouseEvent event) {
        startPoint = new Point2D(event.getSceneX(), event.getSceneY());
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

        Point2D currentPosition = new Point2D(event.getSceneX(), event.getSceneY());
        Point2D delta = currentPosition.subtract(startPoint);

        if (delta.getX() < 0) {
            view.setLayoutX(currentPosition.getX());
        }

        if (delta.getY() < 0) {
            view.setLayoutY(currentPosition.getY());
        }

        view.setMinSize(Math.abs(delta.getX()), Math.abs(delta.getY()));

    }

    private void updateSelection() {
        var workspace = context.activeWorkspace();
        
        Point2D selectionMin = new Point2D(view.getLayoutX(), view.getLayoutY());
        Point2D selectionMax = new Point2D(view.getLayoutX() + view.getWidth(), view.getLayoutY() + view.getHeight());
        var command = workspace.commandFactory().createRectangleSelectCommand(selectionMin, selectionMax);
        workspace.actionManager().executeCommand(command);
    }

    private void removeSelectionRectangle() {
        view.setVisible(false);
    }

}

package blocksmith.ui.editor.selection;

import blocksmith.ui.UiApp;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import blocksmith.app.command.CommandDispatcher;
import blocksmith.ui.editor.EditorEventRouter;
import blocksmith.app.command.Command;
import blocksmith.ui.editor.selection.command.RectangleSelectCommand;
import blocksmith.ui.workspace.WorkspaceFxRegistry;
import static blocksmith.ui.utils.EditorUtils.onFreeSpace;

/**
 *
 * @author joostmeulenkamp
 */
public class SelectionRectangleController {

    private final CommandDispatcher commandDispatcher;
    private final EditorEventRouter eventRouter;
    private final WorkspaceFxRegistry workspaces;
    private final SelectionRectangleView view;

    private Point2D startPoint;

    public SelectionRectangleController(
            CommandDispatcher commandDispatcher,
            EditorEventRouter eventRouter,
            WorkspaceFxRegistry workspaces,
            SelectionRectangleView selectionRectangleView) {

        this.commandDispatcher = commandDispatcher;
        this.eventRouter = eventRouter;
        this.workspaces = workspaces;
        this.view = selectionRectangleView;

        eventRouter.addEventListener(MouseEvent.MOUSE_PRESSED, this::handleSelectionStarted);
        eventRouter.addEventListener(MouseEvent.MOUSE_DRAGGED, this::handleSelectionUpdated);
        eventRouter.addEventListener(MouseEvent.MOUSE_RELEASED, this::handleSelectionFinished);
    }

    public void handleSelectionStarted(MouseEvent event) {
        var workspace = workspaces.active();
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
        var workspace = workspaces.active();
        boolean isPrimary = event.getButton() == MouseButton.PRIMARY;
        boolean isSelecting = workspace.state().isSelecting();

        if (isSelecting && isPrimary) {
            initializeSelectionRectangle();
            updateSelectionRectangle(event);
            updateSelection();

        }
    }

    public void handleSelectionFinished(MouseEvent event) {
        var workspace = workspaces.active();

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
                    commandDispatcher.execute(Command.Id.DESELECT_ALL_BLOCKS);
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
        var rectOnWorkspace = workspaces.active().view().sceneToLocal(rectOnScene);

        var selection = workspaces.active().selection();
        var command = new RectangleSelectCommand(selection, rectOnWorkspace);
        commandDispatcher.execute(command);
    }

    private void removeSelectionRectangle() {
        view.setVisible(false);
    }

}

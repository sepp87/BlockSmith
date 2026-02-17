package btscore.editor;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import btscore.editor.context.EditorContext;
import btscore.editor.context.EditorEventRouter;
import static btscore.utils.EditorUtils.onFreeSpace;

/**
 *
 * @author joostmeulenkamp
 */
public class PanController {

    private final EditorEventRouter eventRouter;
    private final EditorContext context;

    private double initialX;
    private double initialY;
    private double initialTranslateX;
    private double initialTranslateY;

    public PanController(EditorEventRouter eventRouter, EditorContext context) {
        this.eventRouter = eventRouter;
        this.context = context;

        eventRouter.addEventListener(MouseEvent.MOUSE_PRESSED, this::handlePanStarted);
        eventRouter.addEventListener(MouseEvent.MOUSE_DRAGGED, this::handlePanUpdated);
        eventRouter.addEventListener(MouseEvent.MOUSE_RELEASED, this::handlePanFinished);
    }

    public void handlePanStarted(MouseEvent event) {
        var workspace = context.activeWorkspace();
        boolean onFreeSpace = onFreeSpace(event);
        boolean isSecondary = event.getButton() == MouseButton.SECONDARY;
        if (onFreeSpace && workspace.state().isIdle() && isSecondary) {
            workspace.state().setPanning();
            initialX = event.getSceneX();
            initialY = event.getSceneY();
            initialTranslateX = workspace.controller().getModel().translateXProperty().get();
            initialTranslateY = workspace.controller().getModel().translateYProperty().get();
        }
    }

    public void handlePanUpdated(MouseEvent event) {
        var workspace = context.activeWorkspace();
        boolean isSecondary = event.getButton() == MouseButton.SECONDARY;
        if (workspace.state().isPanning() && isSecondary) {
            workspace.controller().getModel().translateXProperty().set(initialTranslateX + event.getSceneX() - initialX);
            workspace.controller().getModel().translateYProperty().set(initialTranslateY + event.getSceneY() - initialY);
        }
    }

    public void handlePanFinished(MouseEvent event) {
        var workspace = context.activeWorkspace();
        if (workspace.state().isPanning() && event.getButton() == MouseButton.SECONDARY) {
            workspace.state().setIdle();
        }
    }

}

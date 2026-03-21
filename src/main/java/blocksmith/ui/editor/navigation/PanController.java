package blocksmith.ui.editor.navigation;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import blocksmith.ui.workspace.FxWorkspaceRegistry;
import blocksmith.ui.editor.EditorEventRouter;
import static blocksmith.ui.utils.EditorUtils.onFreeSpace;

/**
 *
 * @author joostmeulenkamp
 */
public class PanController {

    private final EditorEventRouter eventRouter;
    private final FxWorkspaceRegistry context;

    private double initialX;
    private double initialY;
    private double initialTranslateX;
    private double initialTranslateY;

    public PanController(EditorEventRouter eventRouter, FxWorkspaceRegistry context) {
        this.eventRouter = eventRouter;
        this.context = context;

        eventRouter.addEventListener(MouseEvent.MOUSE_PRESSED, this::handlePanStarted);
        eventRouter.addEventListener(MouseEvent.MOUSE_DRAGGED, this::handlePanUpdated);
        eventRouter.addEventListener(MouseEvent.MOUSE_RELEASED, this::handlePanFinished);
    }

    public void handlePanStarted(MouseEvent event) {
        var workspace = context.active();
        boolean onFreeSpace = onFreeSpace(event);
        boolean isSecondary = event.getButton() == MouseButton.SECONDARY;
        if (onFreeSpace && workspace.state().isIdle() && isSecondary) {
            workspace.state().setPanning();
            initialX = event.getSceneX();
            initialY = event.getSceneY();
            initialTranslateX = workspace.session().viewport().translateXProperty().get();
            initialTranslateY = workspace.session().viewport().translateYProperty().get();
        }
    }

    public void handlePanUpdated(MouseEvent event) {
        var workspace = context.active();
        boolean isSecondary = event.getButton() == MouseButton.SECONDARY;
        if (workspace.state().isPanning() && isSecondary) {
            workspace.session().viewport().translateXProperty().set(initialTranslateX + event.getSceneX() - initialX);
            workspace.session().viewport().translateYProperty().set(initialTranslateY + event.getSceneY() - initialY);
        }
    }

    public void handlePanFinished(MouseEvent event) {
        var workspace = context.active();
        if (workspace.state().isPanning() && event.getButton() == MouseButton.SECONDARY) {
            workspace.state().setIdle();
        }
    }

}

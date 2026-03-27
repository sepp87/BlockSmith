package blocksmith.ui.editor.navigation;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import blocksmith.ui.workspace.WorkspaceFxRegistry;
import blocksmith.ui.editor.EditorEventRouter;
import static blocksmith.ui.utils.EditorUtils.onFreeSpace;

/**
 *
 * @author joostmeulenkamp
 */
public class PanController {

    private final EditorEventRouter eventRouter;
    private final WorkspaceFxRegistry context;

    private double initialX;
    private double initialY;
    private double initialTranslateX;
    private double initialTranslateY;

    public PanController(EditorEventRouter eventRouter, WorkspaceFxRegistry context) {
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
            initialTranslateX = workspace.session().viewport().translateX();
            initialTranslateY = workspace.session().viewport().translateY();
        }
    }

    public void handlePanUpdated(MouseEvent event) {
        var workspace = context.active();
        boolean isSecondary = event.getButton() == MouseButton.SECONDARY;
        if (workspace.state().isPanning() && isSecondary) {
            var tx = initialTranslateX + event.getSceneX() - initialX;
            var ty = initialTranslateY + event.getSceneY() - initialY;
            var update = workspace.session().viewport().withTranslation(tx, ty);
            workspace.session().updateViewport(update);
        }
    }

    public void handlePanFinished(MouseEvent event) {
        var workspace = context.active();
        if (workspace.state().isPanning() && event.getButton() == MouseButton.SECONDARY) {
            workspace.state().setIdle();
        }
    }

}

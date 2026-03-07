package blocksmith.ui.workspace;

import blocksmith.app.outbound.WorkspaceHandle;
import blocksmith.app.outbound.WorkspaceRegistry;
import blocksmith.ui.UiApp;
import blocksmith.ui.editor.EditorView;
import static blocksmith.ui.utils.EditorUtils.onFreeSpace;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author joost
 */
public class FxWorkspaceRegistry implements WorkspaceRegistry {

    // commandfactory (global context) > to get current workspace
    private FxWorkspaceHandle activeWorkspace;
    private final List<Consumer<FxWorkspaceHandle>> activeWorkspaceListeners = new ArrayList<>();

    private final EditorView editorView;
    private Point2D mousePosition = new Point2D(0, 0); // EDITOR

    private final ChangeListener<Object> setupMouseTracking = this::setupMouseTracking;
    private final EventHandler<MouseEvent> trackMouse = this::trackMouse;

    public FxWorkspaceRegistry(EditorView editorView) {
        this.editorView = editorView;
        editorView.sceneProperty().addListener(setupMouseTracking);
    }

    private void setupMouseTracking(Object b, Object o, Object n) {
        editorView.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, trackMouse);
        editorView.getScene().addEventFilter(MouseEvent.MOUSE_MOVED, trackMouse);
        editorView.getScene().addEventFilter(MouseEvent.MOUSE_DRAGGED, trackMouse);
        editorView.getScene().addEventFilter(MouseEvent.MOUSE_RELEASED, trackMouse);
//        editorView.getScene().addEventFilter(MouseEvent.ANY, trackMouseAndKeyboard);
//        editorView.getScene().addEventFilter(KeyEvent.ANY, trackKeyboard);
    }

    private void trackMouse(MouseEvent event) {
        mousePosition = new Point2D(event.getSceneX(), event.getSceneY());
//        System.out.println("SCENE:" + mousePosition + "   EDITOR:" + editorView.sceneToLocal(mousePosition));

        boolean isEditorFocused = editorView.getScene().focusOwnerProperty().get() instanceof EditorView;
        if (!isEditorFocused && onFreeSpace(event)) {
            if (UiApp.LOG_FOCUS_OWNER) {
                System.out.println("FOCUS OWNER: " + editorView.getScene().focusOwnerProperty().get().getClass().getSimpleName());

            }
            editorView.requestFocus();
        }

    }

    public void add(WorkspaceHandle workspace) {
        if (workspace instanceof FxWorkspaceHandle fxWorkspace) {
            activeWorkspace = fxWorkspace;
            activeWorkspaceChanged();
        } else {
            throw new IllegalStateException("Only FX workspace handles allowed");
        }
    }

    @Override
    public void remove(String id) {

    }

    @Override
    public void activate(String id) {

    }

    public void setOnActiveWorkspaceChanged(Consumer<FxWorkspaceHandle> listener) {
        activeWorkspaceListeners.add(listener);
    }

    private void activeWorkspaceChanged() {
        activeWorkspaceListeners.forEach(c -> c.accept(activeWorkspace));
    }

    public FxWorkspaceHandle active() {
        return activeWorkspace;
    }

    public Point2D getMousePositionOnWorkspace() {
        return activeWorkspace.view().sceneToLocal(mousePosition);
    }

    public Point2D sceneToWorkspace(Point2D sceneCoordinates) {
        return activeWorkspace.view().sceneToLocal(sceneCoordinates);
    }

}

package btscore.editor.context;

import btscore.workspace.WorkspaceContext;
import btscore.editor.EditorView;
import static btscore.utils.EditorUtils.onFreeSpace;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author joost
 */
public class EditorContext {

    // commandfactory (global context) > to get current workspace
    private WorkspaceContext activeWorkspace;
    private List<WorkspaceContext> workspaces = new ArrayList<>();

    private final EditorView editorView;
    private EditorEventRouter eventRouter;
    private Point2D mousePosition = new Point2D(0, 0); // EDITOR

    private final ChangeListener<Object> setupMouseTracking = this::setupMouseTracking;
    private final EventHandler<MouseEvent> trackMouse = this::trackMouse;

    public EditorContext(EditorView editorView) {
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

        boolean isEditorFocused = editorView.getScene().focusOwnerProperty().get() instanceof EditorView;
        if (!isEditorFocused && onFreeSpace(event)) {
            editorView.requestFocus();
        }
    }

    public void addWorkspace(WorkspaceContext workspace) {
        activeWorkspace = workspace;
        workspaces.add(workspace);
    }

    public WorkspaceContext activeWorkspace() {
        return activeWorkspace;
    }

    public Point2D getMousePositionOnWorkspace() {
        return activeWorkspace.controller().getView().sceneToLocal(mousePosition);
    }

    public Point2D sceneToWorkspace(Point2D sceneCoordinates) {
        return activeWorkspace.controller().getView().sceneToLocal(sceneCoordinates);
    }

}

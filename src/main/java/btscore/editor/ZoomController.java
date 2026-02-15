package btscore.editor;

import btscore.workspace.WorkspaceModel;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import btscore.UiApp;
import btscore.Config;
import btscore.utils.NodeHierarchyUtils;
import btscore.utils.SystemUtils;
import btscore.editor.context.ActionManager;
import btscore.editor.context.EditorEventRouter;
import btscore.workspace.WorkspaceState;
import btscore.editor.context.Command;
import btscore.editor.context.EditorContext;

/**
 * Manages zooming functionality and controls in the workspace.
 */
public class ZoomController {

    private final EditorEventRouter eventRouter;
    private final EditorContext editorContext;
//    private final ActionManager actionManager;
//    private final WorkspaceStateManager state;
//    private final WorkspaceModel model;
    private final ZoomView view;

    // To throttle zoom on macOS
    private long lastZoomTime = 0;
    private final long zoomThrottleInterval = 50;  // Throttle time in milliseconds (tune for macOS)

    public ZoomController(EditorEventRouter eventRouter, EditorContext editorContext, WorkspaceModel workspaceModel, ZoomView zoomView) {
        this.eventRouter = eventRouter;
        this.editorContext = editorContext;
//        this.actionManager = UiApp.getContext(contextId).getActionManager();
//        this.state = UiApp.getContext(contextId).getStateManager();
//        this.model = workspaceModel;
        this.view = zoomView;

        view.getZoomInButton().setOnAction(this::handleZoomIn);
        view.getZoomOutButton().setOnAction(this::handleZoomOut);
        view.getZoomLabel().setOnMouseClicked(this::handleZoomReset);  // Reset zoom to 100% on click
        view.getZoomLabel().textProperty().bind(workspaceModel.zoomFactorProperty().multiply(100).asString("%.0f%%"));

        eventRouter.addEventListener(ScrollEvent.SCROLL_STARTED, this::handleScrollStarted);
        eventRouter.addEventListener(ScrollEvent.SCROLL, this::handleScrollUpdated);
        eventRouter.addEventListener(ScrollEvent.SCROLL_FINISHED, this::handleScrollFinished);
    }

    private void handleZoomIn(ActionEvent event) {
        var workspace = editorContext.activeWorkspace();
        var command = workspace.commandFactory().createCommand(Command.Id.ZOOM_IN);
        workspace.actionManager().executeCommand(command);
    }

    private void handleZoomOut(ActionEvent event) {
        var workspace = editorContext.activeWorkspace();
        var command = workspace.commandFactory().createCommand(Command.Id.ZOOM_OUT);
        workspace.actionManager().executeCommand(command);
    }

    private void handleZoomReset(MouseEvent event) {
        // Zoom is not from scrolling; no pivot point needed, since scene center is
        var workspace = editorContext.activeWorkspace();

        var command = workspace.commandFactory().createZoomCommand(1.0, null);
        workspace.actionManager().executeCommand(command);
    }

    public void handleScrollStarted(ScrollEvent event) {
        // Scroll started is not triggered on Mac with a normal mouse
        var workspace = editorContext.activeWorkspace();
        if (workspace.state().isIdle()) {
            workspace.state().setZooming();
        }
    }

    // Create and return the ScrollEvent handler for SCROLL
    public void handleScrollUpdated(ScrollEvent event) {

        var workspace = editorContext.activeWorkspace();
        
        boolean onMac = Config.get().operatingSystem() == SystemUtils.OperatingSystem.MACOS;
        boolean isZoomModeAndOnMac = workspace.state().isZooming() && onMac;
        boolean isIdleAndNotOnMac = workspace.state().isIdle() && !onMac;

        Node intersectedNode = event.getPickResult().getIntersectedNode();
        boolean onScrollPane = NodeHierarchyUtils.isNodeOrParentOfType(intersectedNode, ScrollPane.class);
        boolean onListView = NodeHierarchyUtils.isNodeOrParentOfType(intersectedNode, ListView.class);
        boolean onTableView = NodeHierarchyUtils.isNodeOrParentOfType(intersectedNode, TableView.class);

//        if (!onScrollPane && !onListView && (isZoomModeAndOnMac || isIdleAndNotOnMac)) {
        if (!onScrollPane && !onListView && !onTableView && (workspace.state().isIdle() || isIdleAndNotOnMac)) {

            // Throttle zoom on macOS
            if (onMac) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastZoomTime < zoomThrottleInterval) {
                    return;  // Ignore if throttling is active
                }
                lastZoomTime = currentTime;  // Update the last zoom time
            }

            double newScale;
            // Adjust zoom factor based on scroll direction
            if (event.getDeltaY() > 0) {
                newScale = workspace.model().getIncrementedZoomFactor();
            } else {
                newScale = workspace.model().getDecrementedZoomFactor();
            }
            Point2D pivotPoint = new Point2D(event.getSceneX(), event.getSceneY());

            // Zoom from scrolling; keep zoom centered around mouse position
            var command = workspace.commandFactory().createZoomCommand(newScale, pivotPoint);
            workspace.actionManager().executeCommand(command);
        }
    }

    public void handleScrollFinished(ScrollEvent event) {
        var workspace = editorContext.activeWorkspace();
        if (workspace.state().isZooming()) {
            workspace.state().setIdle();
        }
    }

}

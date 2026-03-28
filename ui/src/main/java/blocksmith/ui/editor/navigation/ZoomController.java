package blocksmith.ui.editor.navigation;

import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import blocksmith.Config;
import blocksmith.ui.utils.NodeHierarchyUtils;
import blocksmith.app.command.CommandDispatcher;
import blocksmith.ui.editor.EditorEventRouter;
import blocksmith.app.command.Command;
import blocksmith.app.workspace.ViewportState;
import blocksmith.ui.editor.navigation.command.ZoomCommand;
import blocksmith.ui.workspace.WorkspaceFxRegistry;
import blocksmith.utils.OperatingSystem;
import javafx.beans.property.DoubleProperty;

/**
 * Manages zooming functionality and controls in the workspace.
 */
public class ZoomController {

    private final CommandDispatcher commandDispatcher;
    private final EditorEventRouter eventRouter;
    private final WorkspaceFxRegistry workspaces;
    private final ZoomMenuView view;

    // To throttle zoom on macOS
    private long lastZoomTime = 0;
    private final long zoomThrottleInterval = 50;  // Throttle time in milliseconds (tune for macOS)

    public ZoomController(
            CommandDispatcher commandDispatcher,
            EditorEventRouter eventRouter,
            WorkspaceFxRegistry workspaces,
            ZoomMenuView zoomView) {

        this.commandDispatcher = commandDispatcher;
        this.eventRouter = eventRouter;
        this.workspaces = workspaces;
        this.view = zoomView;

        view.getZoomInButton().setOnAction(this::handleZoomIn);
        view.getZoomOutButton().setOnAction(this::handleZoomOut);
        view.getZoomLabel().setOnMouseClicked(this::handleZoomReset);  // Reset zoom to 100% on click

        eventRouter.addEventListener(ScrollEvent.SCROLL_STARTED, this::handleScrollStarted);
        eventRouter.addEventListener(ScrollEvent.SCROLL, this::handleScrollUpdated);
        eventRouter.addEventListener(ScrollEvent.SCROLL_FINISHED, this::handleScrollFinished);
    }

    public void bindZoomLabel(DoubleProperty zoomFactorProperty) {
        view.getZoomLabel().textProperty().bind(zoomFactorProperty.multiply(100).asString("%.0f%%"));
    }

    private void handleZoomIn(ActionEvent event) {
        commandDispatcher.execute(Command.Id.ZOOM_IN);
    }

    private void handleZoomOut(ActionEvent event) {
        commandDispatcher.execute(Command.Id.ZOOM_OUT);
    }

    private void handleZoomReset(MouseEvent event) {
        // Zoom is not from scrolling; no pivot point needed, since scene center is
        var zoomService = workspaces.active().zoom();
        var command = new ZoomCommand(zoomService, ViewportState.DEFAULT_ZOOM_FACTOR, null);
        commandDispatcher.execute(command);
    }

    public void handleScrollStarted(ScrollEvent event) {
        // Scroll started is not triggered on Mac with a normal mouse
        var workspace = workspaces.active();
        if (workspace.state().isIdle()) {
            workspace.state().setZooming();
        }
    }

    // Create and return the ScrollEvent handler for SCROLL
    public void handleScrollUpdated(ScrollEvent event) {

        var workspace = workspaces.active();

        boolean onMac = Config.get().operatingSystem() == OperatingSystem.MACOS;
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
                newScale = workspace.zoom().getIncrementedZoomFactor();
            } else {
                newScale = workspace.zoom().getDecrementedZoomFactor();
            }
            Point2D pivotPoint = new Point2D(event.getSceneX(), event.getSceneY());

            // Zoom from scrolling; keep zoom centered around mouse position
            var zoomService = workspace.zoom();
            var command = new ZoomCommand(zoomService, newScale, pivotPoint);

            commandDispatcher.execute(command);
        }
    }

    public void handleScrollFinished(ScrollEvent event) {
        var workspace = workspaces.active();
        if (workspace.state().isZooming()) {
            workspace.state().setIdle();
        }
    }

}

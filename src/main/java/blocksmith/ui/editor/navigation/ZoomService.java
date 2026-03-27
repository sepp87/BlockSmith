package blocksmith.ui.editor.navigation;

import blocksmith.app.workspace.ViewportState;
import blocksmith.app.workspace.SelectionState;
import static blocksmith.app.workspace.ViewportState.MAX_ZOOM;
import static blocksmith.app.workspace.ViewportState.MIN_ZOOM;
import static blocksmith.app.workspace.ViewportState.ZOOM_STEP;
import blocksmith.app.workspace.WorkspaceSession;
import blocksmith.ui.projection.GraphProjection;
import blocksmith.ui.geom.GeomUtils;
import blocksmith.ui.graph.block.MethodBlockNew;
import blocksmith.ui.workspace.WorkspaceView;
import blocksmith.ui.workspace.WorkspaceView;
import java.util.Collection;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Scene;

/**
 *
 * @author joostmeulenkamp
 */
public class ZoomService {

    private final WorkspaceSession workspace;
    private final SelectionState selection;
    private final WorkspaceView view;
    private final GraphProjection projection;

    public ZoomService(
            WorkspaceSession workspace,
            SelectionState selection,
            WorkspaceView workspaceView,
            GraphProjection projection) {

        this.workspace = workspace;
        this.selection = selection;
        this.view = workspaceView;
        this.projection = projection;
    }

    public void zoomIn() {
        double newScale = getIncrementedZoomFactor();
        applyZoom(newScale); // Zoom is not from scrolling; no scroll event needed
    }

    public void zoomOut() {
        double newScale = getDecrementedZoomFactor();
        applyZoom(newScale); // Zoom is not from scrolling; no scroll event needed
    }

    public double getIncrementedZoomFactor() {
        var viewport = workspace.viewport();
        return Math.min(MAX_ZOOM, viewport.zoomFactor() + ZOOM_STEP);
    }

    public double getDecrementedZoomFactor() {
        var viewport = workspace.viewport();
        return Math.max(MIN_ZOOM, viewport.zoomFactor() - ZOOM_STEP);
    }

    private void applyZoom(double newScale) {
        zoomToPoint(newScale, null);
    }

    public void zoomToPoint(double newScale, Point2D pivotPoint) {
        var viewport = workspace.viewport();

        double oldScale = viewport.zoomFactor();
        double scaleChange = (newScale / oldScale) - 1;

        // Get the bounds of the workspace
        Bounds workspaceBounds = view.getBoundsInParent();

        double dx, dy;

        if (pivotPoint != null) {
            // Calculate the distance from the zoom point (mouse cursor/graph center) to the workspace origin
            dx = pivotPoint.getX() - workspaceBounds.getMinX();
            dy = pivotPoint.getY() - workspaceBounds.getMinY();
        } else {
            // Calculate the center of the scene (visible area)
            double sceneCenterX = view.getScene().getWidth() / 2;
            double sceneCenterY = view.getScene().getHeight() / 2;

            // Calculate the distance from the workspace's center to the scene's center
            dx = sceneCenterX - workspaceBounds.getMinX();
            dy = sceneCenterY - workspaceBounds.getMinY();
        }

        // Calculate the new translation needed to zoom to the center or to the mouse position
        double dX = scaleChange * dx;
        double dY = scaleChange * dy;

        double newTranslateX = viewport.translateX() - dX;
        double newTranslateY = viewport.translateY() - dY;

        var update = viewport
                .withZoomFactor(newScale)
                .withTranslation(newTranslateX, newTranslateY);
        workspace.updateViewport(update);
    }

    public void zoomToFit() {
        var ids = selection.selected();

        if (ids.isEmpty()) { // zoom to fit all
            var all = projection.blocks();
            zoomToFit(all);

        } else {
            var selection = projection.blocks(ids);
            zoomToFit(selection);
        }
    }

    private void zoomToFit(Collection<MethodBlockNew> blocks) {

        if (blocks.isEmpty()) {
            return;
        }

        var viewport = workspace.viewport();
        Scene scene = view.getScene();

        Bounds boundingBox = view.localToParent(GeomUtils.boundsOf(blocks.stream().map(b -> b.measuredBounds()).toList()));

        // compute scale
        double ratioX = boundingBox.getWidth() / scene.getWidth();
        double ratioY = boundingBox.getHeight() / scene.getHeight();
        double ratio = Math.max(ratioX, ratioY);

        // multiply, round and divide by 10 to reach zoom step of 0.1 and substract by 1 to zoom a bit more out so the blocks don't touch the border
        double newScale = Math.ceil((viewport.zoomFactor() / ratio) * 10 - 1) / 10.0;
        newScale = Math.max(ViewportState.MIN_ZOOM, Math.min(ViewportState.MAX_ZOOM, newScale));

        // compute scale change
        double oldScale = viewport.zoomFactor();
        double scaleChange = newScale / oldScale;

        // compute centers (OLD coordinate system)
        double sceneCenterX = scene.getWidth() / 2;
        double sceneCenterY = scene.getHeight() / 2;

        // adjust translation WITH scale
        double newTranslateX = (viewport.translateX() - boundingBox.getCenterX()) * scaleChange + sceneCenterX;
        double newTranslateY = (viewport.translateY() - boundingBox.getCenterY()) * scaleChange + sceneCenterY;

        var next = viewport
                .withZoomFactor(newScale)
                .withTranslation(newTranslateX, newTranslateY);

        workspace.updateViewport(next);
    }

}

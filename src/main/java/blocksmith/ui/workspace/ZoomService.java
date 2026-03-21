package blocksmith.ui.workspace;

import blocksmith.ui.projection.GraphProjection;
import blocksmith.ui.geom.GeomUtils;
import blocksmith.ui.graph.block.MethodBlockNew;
import java.util.Collection;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Scene;

/**
 *
 * @author joostmeulenkamp
 */
public class ZoomService {

    private final Viewport viewport;
    private final SelectionModel selection;
    private final WorkspaceView view;
    private final GraphProjection projection;

    public ZoomService(
            Viewport viewport,
            SelectionModel selection,
            WorkspaceView workspaceView,
            GraphProjection projection) {

        this.viewport = viewport;
        this.selection = selection;
        this.view = workspaceView;
        this.projection = projection;

        bind();
    }

    private final void bind() {
        view.scaleXProperty().bind(viewport.zoomFactorProperty());
        view.scaleYProperty().bind(viewport.zoomFactorProperty());
        view.translateXProperty().bind(viewport.translateXProperty());
        view.translateYProperty().bind(viewport.translateYProperty());
    }

    public void zoomIn() {
        double newScale = viewport.getIncrementedZoomFactor();
        applyZoom(newScale); // Zoom is not from scrolling; no scroll event needed
    }

    public void zoomOut() {
        double newScale = viewport.getDecrementedZoomFactor();
        applyZoom(newScale); // Zoom is not from scrolling; no scroll event needed
    }

    private void applyZoom(double newScale) {
        zoomToPoint(newScale, null);
    }

    public void zoomToPoint(double newScale, Point2D pivotPoint) {

        double oldScale = viewport.zoomFactorProperty().get();
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

        double newTranslateX = viewport.translateXProperty().get() - dX;
        double newTranslateY = viewport.translateYProperty().get() - dY;

        viewport.translateXProperty().set(newTranslateX);
        viewport.translateYProperty().set(newTranslateY);
        viewport.zoomFactorProperty().set(newScale);
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

        Scene scene = view.getScene();
        if (blocks.isEmpty()) {
            return;
        }

        //Zoom to fit        
        Bounds boundingBox = view.localToParent(GeomUtils.boundsOf(blocks.stream().map(b -> b.measuredBounds()).toList()));
        double ratioX = boundingBox.getWidth() / scene.getWidth();
        double ratioY = boundingBox.getHeight() / scene.getHeight();
        double ratio = Math.max(ratioX, ratioY);
        // multiply, round and divide by 10 to reach zoom step of 0.1 and substract by 1 to zoom a bit more out so the blocks don't touch the border
        double scale = Math.ceil((viewport.zoomFactorProperty().get() / ratio) * 10 - 1) / 10;
        scale = scale < Viewport.MIN_ZOOM ? Viewport.MIN_ZOOM : scale;
        scale = scale > Viewport.MAX_ZOOM ? Viewport.MAX_ZOOM : scale;
        viewport.zoomFactorProperty().set(scale);

        //Pan to fit
        boundingBox = view.localToParent(GeomUtils.boundsOf(blocks.stream().map(b -> b.measuredBounds()).toList()));
        double dx = (boundingBox.getMinX() + boundingBox.getWidth() / 2) - scene.getWidth() / 2;
        double dy = (boundingBox.getMinY() + boundingBox.getHeight() / 2) - scene.getHeight() / 2;
        double newTranslateX = viewport.translateXProperty().get() - dx;
        double newTranslateY = viewport.translateYProperty().get() - dy;

        viewport.translateXProperty().set(newTranslateX);
        viewport.translateYProperty().set(newTranslateY);
    }
}

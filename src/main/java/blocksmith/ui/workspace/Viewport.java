package blocksmith.ui.workspace;

import blocksmith.app.GraphDocument;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 *
 * @author joost
 */
public class Viewport {

    public static final double DEFAULT_ZOOM_FACTOR = 1.0;
    public static final double MAX_ZOOM = 1.5;
    public static final double MIN_ZOOM = 0.3;
    public static final double ZOOM_STEP = 0.1;

    private final DoubleProperty zoomFactor = new SimpleDoubleProperty(DEFAULT_ZOOM_FACTOR);
    private final DoubleProperty translateX = new SimpleDoubleProperty(0.);
    private final DoubleProperty translateY = new SimpleDoubleProperty(0.);

    public Viewport(GraphDocument document) {
        this.zoomFactor.set(document.zoomFactor());
        this.translateX.set(document.translateX());
        this.translateY.set(document.translateY());
    }

    public DoubleProperty zoomFactorProperty() {
        return zoomFactor;
    }

    public DoubleProperty translateXProperty() {
        return translateX;
    }

    public DoubleProperty translateYProperty() {
        return translateY;
    }

    public void resetZoomFactor() {
        zoomFactor.set(DEFAULT_ZOOM_FACTOR);
    }

    // Increment zoom factor by the defined step size
    public double getIncrementedZoomFactor() {
        return Math.min(MAX_ZOOM, zoomFactor.get() + ZOOM_STEP);
    }

    // Decrement zoom factor by the defined step size
    public double getDecrementedZoomFactor() {
        return Math.max(MIN_ZOOM, zoomFactor.get() - ZOOM_STEP);
    }

    public void setZoomFactor(double factor) {
        this.zoomFactor.set(Math.round(factor * 10) / 10.);
    }

    public void reset() {
        resetZoomFactor();
        translateXProperty().set(0.);
        translateYProperty().set(0.);
    }
}

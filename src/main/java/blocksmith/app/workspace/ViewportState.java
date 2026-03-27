package blocksmith.app.workspace;

import blocksmith.app.GraphDocument;

/**
 *
 * @author joost
 */
public record ViewportState(
        double zoomFactor,
        double translateX,
        double translateY) {

    public static final double DEFAULT_ZOOM_FACTOR = 1.0;
    public static final double DEFAULT_TRANSLATE = 0.0;
    public static final double MAX_ZOOM = 1.5;
    public static final double MIN_ZOOM = 0.3;
    public static final double ZOOM_STEP = 0.1;

    public static ViewportState of(GraphDocument document) {
        return new ViewportState(
                document.zoomFactor(),
                document.translateX(),
                document.translateY()
        );
    }

    public ViewportState withZoomFactor(double zoomFactor) {
        return new ViewportState(
                round(zoomFactor),
                translateX,
                translateY
        );
    }

    private double round(double value) {
        return Math.round(value * 10) / 10.;
    }

    public ViewportState withTranslation(double translateX, double translateY) {
        return new ViewportState(
                zoomFactor,
                translateX,
                translateY
        );
    }

    public ViewportState reset() {
        return createDefault();
    }

    public static ViewportState createDefault() {
        return new ViewportState(
                DEFAULT_ZOOM_FACTOR,
                DEFAULT_TRANSLATE,
                DEFAULT_TRANSLATE
        );
    }
    
    
}

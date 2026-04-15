package blocksmith.ui.workspace;

import blocksmith.app.workspace.ViewportState;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 *
 * @author joost
 */
public class ViewportFxModel {

    private final DoubleProperty zoomFactor = new SimpleDoubleProperty();
    private final DoubleProperty translateX = new SimpleDoubleProperty();
    private final DoubleProperty translateY = new SimpleDoubleProperty();

    public ViewportFxModel(ViewportState state) {
        onViewportChanged(state);
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

    public void onViewportChanged(ViewportState viewport) {
        zoomFactor.set(viewport.zoomFactor());
        translateX.set(viewport.translateX());
        translateY.set(viewport.translateY());
    }

}

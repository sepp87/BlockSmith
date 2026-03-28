package blocksmith.ui.graph.port;

import blocksmith.domain.value.Port;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;

/**
 *
 * @author JoostMeulenkamp
 */
public class PortView extends VBox {

    private final Tooltip tooltip;
    private final Port.Direction direction;
    private final DoubleProperty centerX = new SimpleDoubleProperty(-1);
    private final DoubleProperty centerY = new SimpleDoubleProperty(-1);

    public PortView(Port.Direction direction) {
        this.direction = direction;
        this.tooltip = new Tooltip();
        Tooltip.install(this, tooltip);

        getStyleClass().add("port");
        getStyleClass().add("port-" + direction.toString().toLowerCase());
    }

    public Tooltip getTooltip() {
        return tooltip;
    }

    public void setActive(boolean isActive) {
        getStyleClass().removeAll("port", "port-active");
        getStyleClass().add(isActive ? "port-active" : "port");
    }
    
    public DoubleProperty centerXProperty() {
        return centerX;
    }
    
    public DoubleProperty centerYProperty() {
        return centerY;
    }

 
}

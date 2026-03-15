package blocksmith.ui.display;

import blocksmith.domain.value.Port.Direction;
import java.util.List;
import javafx.scene.Node;

/**
 *
 * @author joost
 */
public class ValueInspector {

    private final Direction direction;
    private final String valueId;
    private final ValueDisplay display;

    private List<?> values = List.of();
    private int current = 0;

    public ValueInspector(Direction direction, String valueId, ValueDisplay display) {
        this.direction = direction;
        this.valueId = valueId;
        this.display = display;
    }

    public Direction direction() {
        return direction;
    }

    public String valueId() {
        return valueId;
    }

    public void setData(Object object) {
        current = 0;

        if (object instanceof List<?> list) {
            values = list;
        } else {
            values = object == null ? List.of() : List.of(object);
        }
        update();
    }

    private void update() {
        if (display instanceof ValueDisplay.MultiValue multiDisplay) {
            multiDisplay.render(values);

        } else if (display instanceof ValueDisplay.SingleValue singleDisplay) {
            singleDisplay.render(values.get(current));

        }
    }

    public Node node() {
        return display.node();
    }

    public void dispose() {
    }

}

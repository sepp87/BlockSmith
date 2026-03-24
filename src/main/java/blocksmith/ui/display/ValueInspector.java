package blocksmith.ui.display;

import blocksmith.domain.connection.PortRef;
import blocksmith.domain.value.Port.Direction;
import java.util.List;
import javafx.scene.Node;

/**
 *
 * @author joost
 */
public class ValueInspector {

    private final PortRef ref;
    private final ValueDisplay display;

    private List<?> values = List.of();
    private int current = 0;

    public ValueInspector(PortRef ref, ValueDisplay display) {
        this.ref = ref;
        this.display = display;
    }


    public PortRef ref() {
        return ref;
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
            var value = values.size() > current ? values.get(current) : null;
            singleDisplay.render(value);
        }
    }

    public Node node() {
        return display.node();
    }

    public void dispose() {
    }

}

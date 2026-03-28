package blocksmith.ui.display;

import java.util.List;
import javafx.scene.Node;
import javafx.scene.control.TextArea;

/**
 *
 * @author joost
 */
public class GenericDisplay implements ValueDisplay.MultiValue {

    private final TextArea textArea;

    public GenericDisplay() {
        textArea = new TextArea();
        textArea.setMinSize(220, 220);
        textArea.setPrefSize(220, 220);
        textArea.setMaxHeight(Double.MAX_VALUE);
        textArea.setEditable(false);
        textArea.setText("null");

    }

    @Override
    public void render(List<?> values) {

        if (values == null || values.isEmpty()) {
            textArea.setText("null");

        } else if (values.size() == 1) {
            var displayValue = String.valueOf(values.getFirst());
            textArea.setText(displayValue);

        } else {
            var stringValues = values.stream().map(v -> String.valueOf(v)).toList();
            var displayValue = "[" + String.join(", ", stringValues) + "]";
            textArea.setText(displayValue);
        }
    }

    @Override
    public Node node() {
        return textArea;
    }

    @Override
    public void dispose() {
    }

}

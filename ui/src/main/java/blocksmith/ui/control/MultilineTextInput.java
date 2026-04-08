package blocksmith.ui.control;

import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author joost
 */
public class MultilineTextInput extends InputControl<Object> {

    private final TextArea textArea;

    public MultilineTextInput(String valueId) {
        super(valueId);

        textArea = new TextArea();
        textArea.setMinSize(220, 220);
        textArea.setPrefSize(220, 220);
        textArea.setOnKeyPressed(this::ignoreShortcuts);
        textArea.setMaxHeight(Double.MAX_VALUE);
        textArea.textProperty().bindBidirectional(value);
    }

    private void ignoreShortcuts(KeyEvent event) {
        event.consume();
    }

    @Override
    public Node node() {
        return textArea;
    }

    @Override
    public void onValueChangedByApp(String newVal) {
    }

    @Override
    public void onDispose() {
        textArea.setOnKeyPressed(null);
    }

}

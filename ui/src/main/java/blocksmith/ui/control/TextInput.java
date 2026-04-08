package blocksmith.ui.control;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author joostmeulenkamp
 */
public class TextInput extends InputControl<String> {

    private final TextField textField;

    public TextInput(String valueId) {
        super(valueId);
        
        this.textField = new TextField();
        textField.setPromptText("Write here...");
        textField.setFocusTraversable(false);
        textField.setMinWidth(100);
        textField.setStyle("-fx-pref-column-count: 26;");
        textField.setOnKeyPressed(this::ignoreShortcuts);
        textField.textProperty().bindBidirectional(value);
        textField.setOnMouseEntered(eh -> textField.requestFocus());
    }

    private void ignoreShortcuts(KeyEvent event) {
        event.consume();
    }

    @Override
    public Node node() {
        return textField;
    }
    
    @Override
    public void onValueChangedByApp(String newVal) {
    }

    @Override
    public void onDispose() {
        textField.setOnKeyPressed(null);
        textField.setOnMouseEntered(null);
    }
    
}
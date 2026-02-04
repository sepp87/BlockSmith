package blocksmith.ui.control;

import blocksmith.ui.control.InputControl;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author joostmeulenkamp
 */
public class TextInput implements InputControl<String> {

    private final List<Consumer<String>> listeners = new ArrayList<>();
    private final ChangeListener<String> fxListener = (b, o, n) -> listeners.forEach(c -> c.accept(n));
    private final TextField textField;

    public TextInput() {
        this.textField = new TextField();

        textField.setPromptText("Write here...");
        textField.setFocusTraversable(false);
        textField.setMinWidth(100);
        textField.setStyle("-fx-pref-column-count: 26;");
        textField.setOnKeyPressed(this::ignoreShortcuts);
        textField.textProperty().addListener(fxListener);
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
    public String getValue() {
        return textField.getText();
    }

    @Override
    public void setValue(String newVal) {
        if(Objects.equals(textField.getText(), newVal)) {
            return;
        }
        textField.setText(newVal);
    }

    @Override
    public void dispose() {
        textField.textProperty().removeListener(fxListener);
        textField.setOnKeyPressed(null);
        textField.setOnMouseEntered(null);
    }

    @Override
    public void setOnValueChanged(Consumer<String> listener) {
        listeners.add(listener);
    }

    @Override
    public void setEditable(boolean isEditable) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    @Override
    public boolean isEditable() {
        return true;
    }

    @Override
    public InputControl<String> copy() {
        var control = new TextInput();
        if(isEditable()) {
            control.setValue(this.getValue());
        }
        return control;
    }

}

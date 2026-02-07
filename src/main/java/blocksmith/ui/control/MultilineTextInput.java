package blocksmith.ui.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author joost
 */
public class MultilineTextInput extends InputControl<Object> {
    
    private final ChangeListener<String> fxListener = (b, o, n) -> onValueChangedByUser(n);
    
    private TextArea textArea;
    
    public MultilineTextInput() {
        textArea = new TextArea();
        textArea.setMinSize(220, 220);
        textArea.setPrefSize(220, 220);
        textArea.setOnKeyPressed(this::ignoreShortcuts);
        textArea.textProperty().addListener(fxListener);
    }
    
    private void ignoreShortcuts(KeyEvent event) {
        event.consume();
    }
    
    @Override
    public Node node() {
        return textArea;
    }
    
    @Override
    public String getValue() {
        return textArea.getText();
    }
    
    @Override
    public void setValue(Object newVal) {
        if (Objects.equals(this.getValue(), newVal)) {
            return;
        }
        textArea.setText(String.valueOf(newVal));
    }
    
    @Override
    public void dispose() {
        textArea.setOnKeyPressed(null);
        textArea.textProperty().removeListener(fxListener);
    }
    
    protected void onEditableChanged(boolean isEditable) {
        textArea.setEditable(isEditable);        
        if (isEditable) {
            textArea.setText(null);
        }
        // if set uneditable, it is assumed a new value will be set instantly afterwards
    }

    
    @Override
    public InputControl<Object> copy() {
        var control = new MultilineTextInput();
        if (isEditable()) {
            control.setValue(this.getValue());
        }
        return control;
    }
    
}

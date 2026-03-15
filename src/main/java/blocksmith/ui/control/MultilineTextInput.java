package blocksmith.ui.control;

import java.util.Objects;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author joost
 */
public class MultilineTextInput extends InputControl<Object> {

    private final ChangeListener<String> fxListener = (b, o, n) -> valueChangedByUser(n);

//    private final StringProperty value = new SimpleStringProperty();
    private TextArea textArea;

//    private boolean syncing;
    public MultilineTextInput(String valueId) {
        super(valueId);

        textArea = new TextArea();
        textArea.setMinSize(220, 220);
        textArea.setPrefSize(220, 220);
        textArea.setOnKeyPressed(this::ignoreShortcuts);
        textArea.setMaxHeight(Double.MAX_VALUE);
        textArea.textProperty().addListener(fxListener);

//        textArea.textProperty().addListener((b, o, n) -> {
//            if (syncing) {
//                return;
//            }
//            if (!isEditable()) {
//                return;
//            }
//
//            var newValue = n.isEmpty() ? null : n;
//            if (!Objects.equals(value.get(), newValue)) {
//                value.set(newValue);
//            }
//        });
//        value.addListener(fxListener);
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
    public void setValue(String newVal) {

        if (Objects.equals(this.getValue(), newVal)) {
            return;
        }

        textArea.setText(newVal);
//        updateTextArea();
    }

    @Override
    public void onDispose() {
        textArea.setOnKeyPressed(null);
        textArea.textProperty().removeListener(fxListener);

//        value.removeListener(fxListener);
    }
//
//    protected void onValueChanged(String newValue) {
//        updateTextArea();
//    }
//    
//    protected void onEditableChanged(boolean isEditable) {
//        textArea.setEditable(isEditable);
//        updateTextArea();
//    }
//
//    private void updateTextArea() {
//        syncing = true;
//        var displayValue = displayValue();
//        textArea.setText(displayValue);
//        syncing = false;
//    }
//
//    private String displayValue() {
//        if (value.get() != null) {
//            return value.get();
//        }
//        return isEditable() ? "" : "null";
//    }

}

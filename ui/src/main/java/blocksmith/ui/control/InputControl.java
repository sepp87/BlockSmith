package blocksmith.ui.control;

import blocksmith.xml.v2.ValueXml;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import javafx.scene.Node;

/**
 *
 * @author joostmeulenkamp
 */
public abstract class InputControl<T> {

    private final String valueId;
//    private boolean editable = true;

//    protected final StringProperty value = new SimpleStringProperty();
//    protected final ChangeListener<String> valueListener = (b, o, n) -> onValueChanged(n);

    private final List<Consumer<String>> listeners = new ArrayList<>();

    public InputControl(String valueId) {
        this.valueId = valueId;

//        value.addListener(valueListener);
    }

    public String valueId() {
        return valueId;
    }

    public abstract Node node();
//
//    public final ReadOnlyStringProperty valueProperty() {
//        return value;
//    }
//
//    public final void bindValuePropertyTo(ObservableValue<?> target) {
//        var binding = Bindings.createStringBinding(() -> format(target.getValue()), target);
//        value.bind(binding);
//    }
//
//    private String format(Object raw) {
//        return raw == null ? null : raw.toString();
//    }
//
//    public final void unbindValueProperty() {
//        value.unbind();
//    }

//    public final String getValue() {
//        return value.get();
//    }

    public abstract String getValue();

    public abstract void setValue(String newVal);

//    protected abstract void onValueChanged(String newValue);

    public final void setOnValueChangedByUser(Consumer<String> listener) {
        listeners.clear();
        listeners.add(listener);
    }

    protected final void valueChangedByUser(String value) {
//        if (isEditable()) {
            listeners.forEach(c -> c.accept(value));
//        }
    }

//    public void setEditable(boolean isEditable) {
//        if (editable == isEditable) {
//            return;
//        }
//        editable = isEditable;
//        onEditableChanged(isEditable);
//    }
//
//    protected abstract void onEditableChanged(boolean isEditable);
//
//    public boolean isEditable() {
//        return editable;
//    }

    public Optional<ValueXml> serialize() {
//        if (isEditable()) {
            var value = new ValueXml();
            value.setValue(getValue().toString());
            return Optional.ofNullable(value);
//        }
//        return Optional.empty();
    }

    public void dispose() {
//        value.removeListener(valueListener);
//        value.unbind();
        onDispose();
    }

    protected abstract void onDispose();

}

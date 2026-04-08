package blocksmith.ui.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;

/**
 *
 * @author joostmeulenkamp
 */
public abstract class InputControl<T> {

    private final String valueId;
    protected final StringProperty value = new SimpleStringProperty();
    protected final ChangeListener<String> valueListener;
    private boolean syncing = false;

    private final List<Consumer<String>> listeners = new ArrayList<>();

    public InputControl(String valueId) {
        this.valueId = valueId;

        valueListener = (b, o, n) -> {
            if (syncing) {
                return;
            }
            valueChangedByUser(n);
        };
        value.addListener(valueListener);
    }

    public String valueId() {
        return valueId;
    }

    public abstract Node node();

    public final String getValue() {
        return value.get();
    }

    public void setValue(String newVal) {
        if (Objects.equals(value.get(), newVal)) {
            return;
        }
        syncing = true;
        value.set(newVal);
        onValueChangedByApp(newVal);
        syncing = false;
    }

    protected abstract void onValueChangedByApp(String newVal);

    public final void setOnValueChangedByUser(Consumer<String> listener) {
        listeners.clear();
        listeners.add(listener);
    }

    private void valueChangedByUser(String value) {
        listeners.forEach(c -> c.accept(value));
    }

    public void dispose() {
        value.removeListener(valueListener);
        onDispose();
    }

    protected abstract void onDispose();

}
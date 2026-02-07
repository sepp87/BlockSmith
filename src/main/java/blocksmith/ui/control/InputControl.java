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

    private boolean editable = true;
    
    private final List<Consumer<T>> listeners = new ArrayList<>();
    
    public abstract Node node();

    public abstract T getValue();

    public abstract void setValue(T newVal);

    public abstract void dispose();

    public final void setOnValueChangedByUser(Consumer<T> listener) {
        listeners.add(listener);
    }
    
    protected final void onValueChangedByUser(T t) {
        if(isEditable()) {
//                    System.out.println("InputControl.onValueChangedByUser " + t + " " + this.getClass().getSimpleName());

            listeners.forEach(c -> c.accept(t));
        }
    }

    public void setEditable(boolean isEditable) {
        if(editable == isEditable) {
            return;
        }
        editable = isEditable;
        onEditableChanged(isEditable);
    }
    
    protected abstract void onEditableChanged(boolean isEditable);

    public boolean isEditable() {
        return editable;
    }

    public abstract InputControl<T> copy();

    public Optional<ValueXml> serialize() {
        if (isEditable()) {
            var value = new ValueXml();
            value.setValue(getValue().toString());
            return Optional.ofNullable(value);
        }
        return Optional.empty();
    }

    public void parseValue(String newVal) {
        setValue((T) newVal);
    }
}

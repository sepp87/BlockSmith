package blocksmith.ui.control;

import blocksmith.xml.v2.Value;
import java.util.Optional;
import java.util.function.Consumer;
import javafx.scene.Node;

/**
 *
 * @author joostmeulenkamp
 */
public interface InputControl<T> {

    abstract Node node();

    T getValue();

    void setValue(T newVal);

    void dispose();

    void setOnValueChanged(Consumer<T> listener);

    void setEditable(boolean isEditable);

    boolean isEditable();

    InputControl<T> copy();

    default Optional<Value> serialize() {
        if (isEditable()) {
            Value value = new Value();
            value.setValue(getValue().toString());
            return Optional.ofNullable(value);
        }
        return Optional.empty();
    }
    
    default void parseValue(String newVal) {
        setValue((T) newVal);
    }
}

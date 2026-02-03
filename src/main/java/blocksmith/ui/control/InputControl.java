package blocksmith.ui.control;

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
}

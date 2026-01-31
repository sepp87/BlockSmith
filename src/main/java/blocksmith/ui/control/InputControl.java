package blocksmith.ui.control;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import javafx.scene.Node;

/**
 *
 * @author joostmeulenkamp
 */
public interface InputControl<T> {

    public abstract Node node();

    public T getValue();
    
    public void setValue(T value);
    
    public void dispose(); // TODO rename to dispose/cleanup?
    
    public void setOnValueChanged(Consumer<T> listener);
}

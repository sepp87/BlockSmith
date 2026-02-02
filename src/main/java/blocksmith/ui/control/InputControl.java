package blocksmith.ui.control;

import java.util.function.Consumer;
import javafx.scene.Node;

/**
 *
 * @author joostmeulenkamp
 */
public interface InputControl<T> {

    public abstract Node node();

    public T getValue();
    
    public void setValue(T newVal);
    
    public void dispose();
    
    public void setOnValueChanged(Consumer<T> listener);
}

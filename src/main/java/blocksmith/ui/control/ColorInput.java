package blocksmith.ui.control;

import btslib.ui.ColorBox;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.paint.Color;

/**
 *
 * @author joost
 */
public class ColorInput implements InputControl<String> {

    private List<Consumer<String>> listeners = new ArrayList<>();
    private ChangeListener<Color> fxListener = (b, o, n) -> listeners.forEach(c -> c.accept(n.toString()));
    private ObjectProperty<Color> value;

    private ColorBox picker;

    public ColorInput() {
        picker = new ColorBox();
        value = picker.customColorProperty();
        value.addListener(fxListener);
    }

    @Override
    public Node node() {
        return picker;
    }

    @Override
    public String getValue() {
        return value.get().toString();
    }

    @Override
    public void setValue(String newVal) {
        var newColor = Color.valueOf(newVal);
        if (!Objects.equals(value.get(), newColor)) {
            return;
        }
        value.set(newColor);
    }

    @Override
    public void dispose() {
        value.removeListener(fxListener);
    }

    @Override
    public void setOnValueChanged(Consumer<String> listener) {
        listeners.add(listener);
    }

}

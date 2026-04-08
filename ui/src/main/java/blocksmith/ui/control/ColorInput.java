package blocksmith.ui.control;

import blocksmith.ui.control.utils.ColorBox;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.paint.Color;

/**
 *
 * @author joost
 */
public class ColorInput extends InputControl<String> {

    private final ObjectProperty<Color> colorValue;
    private final ChangeListener<Color> colorListener;
    private boolean syncing = false;

    private ColorBox colorPicker;

    public ColorInput(String valueId) {
        super(valueId);

        colorPicker = new ColorBox();
        colorValue = colorPicker.customColorProperty();

        colorListener = (b, o, n) -> {
            if (syncing) {
                return;
            }
            value.set(colorValue.get().toString());
        };

        colorValue.addListener(colorListener);
    }

    @Override
    public Node node() {
        return colorPicker;
    }

    @Override
    public void onValueChangedByApp(String newVal) {
        var newColor = Color.valueOf(newVal);

        syncing = true;
        colorValue.set(newColor);
        syncing = false;
    }

    @Override
    public void onDispose() {
    }

}

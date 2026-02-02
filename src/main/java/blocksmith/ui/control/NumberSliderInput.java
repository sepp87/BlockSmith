package blocksmith.ui.control;

import btslib.ui.NumberSliderExpander;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;

/**
 *
 * @author joost
 */
public abstract class NumberSliderInput implements InputControl<String> {

    private final List<Consumer<String>> listeners = new ArrayList<>();
    private final ChangeListener<Number> fxListener = (b, o, n) -> listeners.forEach(c -> c.accept(n.toString()));

    protected Property<Number> value;
    protected Property<Number> min;
    protected Property<Number> max;
    protected Property<Number> step;

    private final Pane root;
    private final Slider slider;
    private final NumberSliderExpander expander;

    public NumberSliderInput() {
        initializeProperties();

        slider = new Slider(0, 10, 0);
        slider.setBlockIncrement(step.getValue().doubleValue());
        slider.setSnapToTicks(true);
        slider.majorTickUnitProperty().bind(slider.blockIncrementProperty());
        slider.setMinorTickCount(0);

        slider.valueProperty().bindBidirectional(value);
        slider.minProperty().bindBidirectional(min);
        slider.maxProperty().bindBidirectional(max);
        slider.blockIncrementProperty().bindBidirectional(step);

        root = new Pane();
        expander = new NumberSliderExpander(slider, isIntegerSlider(), value, min, max, step);
        expander.setLayoutX(0);
        expander.setLayoutY(0);
        slider.setLayoutX(30);
        slider.setLayoutY(4);
        root.getChildren().addAll(expander, slider);
        root.setOnMouseEntered(eh -> slider.requestFocus());

        value.addListener(fxListener);
    }

    protected abstract void initializeProperties();

    protected abstract boolean isIntegerSlider();
    
    @Override
    public Node node() {
        return root;
    }

    @Override
    public String getValue() {
        return value.getValue().toString();
    }

    @Override
    public void dispose() {
        value.removeListener(fxListener);
        slider.valueProperty().unbindBidirectional(value);
        slider.minProperty().unbindBidirectional(min);
        slider.maxProperty().unbindBidirectional(max);
        slider.blockIncrementProperty().unbindBidirectional(step);
        expander.dispose();
    }

    @Override
    public void setOnValueChanged(Consumer<String> listener) {
        listeners.add(listener);
    }

}

package blocksmith.ui.control;

import blocksmith.ui.control.utils.NumberSliderExpander;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;

/**
 *
 * @author joost
 */
public abstract class NumberSliderInput extends InputControl<String> {

    protected DoubleProperty value = new SimpleDoubleProperty();
    protected DoubleProperty min = new SimpleDoubleProperty();
    protected DoubleProperty max = new SimpleDoubleProperty();
    protected DoubleProperty step = new SimpleDoubleProperty();
    private final boolean isInteger;
    private final ChangeListener<Number> fxListener;

    private final Pane root;
    private final Slider slider;
    private final NumberSliderExpander expander;

    public NumberSliderInput(String valueId, double value, double min, double max, double step, boolean isInteger) {
        super(valueId);

        this.value.set(value);
        this.min.set(min);
        this.max.set(max);
        this.step.set(step);
        this.isInteger = isInteger;
        this.fxListener = (b, o, n) -> valueChangedByUser(isInteger ? String.valueOf(n.intValue()) : n.toString());

        slider = new Slider();
        slider.setBlockIncrement(this.step.getValue().doubleValue());
        slider.setSnapToTicks(true);
        slider.majorTickUnitProperty().bind(slider.blockIncrementProperty());
        slider.setMinorTickCount(0);

        slider.valueProperty().bindBidirectional(this.value);
        slider.minProperty().bindBidirectional(this.min);
        slider.maxProperty().bindBidirectional(this.max);
        slider.blockIncrementProperty().bindBidirectional(this.step);

        root = new Pane();
        expander = new NumberSliderExpander(slider, isInteger, this.value, this.min, this.max, this.step);
        expander.setLayoutX(0);
        expander.setLayoutY(0);
        slider.setLayoutX(30);
        slider.setLayoutY(4);
        root.getChildren().addAll(expander, slider);
        root.setOnMouseEntered(eh -> slider.requestFocus());

        this.value.addListener(fxListener);
    }

    @Override
    public Node node() {
        return root;
    }

    @Override
    public String getValue() {
        if (isInteger) {
            return value.getValue().intValue() + "";
        }
        return value.getValue().toString();
    }

    @Override
    public void setValue(String newVal) {
        newVal = newVal == null ? "0" : newVal;
        var newNum = Double.parseDouble(newVal);
        var oldNum = value.getValue().doubleValue();
        if (Double.compare(newNum, oldNum) == 0) {
            return;
        }
        value.setValue(newNum);
    }

    @Override
    public void onDispose() {
        value.removeListener(fxListener);
        slider.valueProperty().unbindBidirectional(value);
        slider.minProperty().unbindBidirectional(min);
        slider.maxProperty().unbindBidirectional(max);
        slider.blockIncrementProperty().unbindBidirectional(step);
        expander.dispose();
    }

    public void setMin(Number newVal) {
        min.setValue(newVal);
    }

    public void setMax(Number newVal) {
        max.setValue(newVal);
    }

    public void setStep(Number newVal) {
        step.setValue(newVal);
    }

}

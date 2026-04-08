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

    protected DoubleProperty doubleValue = new SimpleDoubleProperty();
    protected DoubleProperty min = new SimpleDoubleProperty();
    protected DoubleProperty max = new SimpleDoubleProperty();
    protected DoubleProperty step = new SimpleDoubleProperty();
    private final boolean isInteger;
    private final ChangeListener<Number> doubleListener;
    private boolean syncing = false;

    private final Pane root;
    private final Slider slider;
    private final NumberSliderExpander expander;

    public NumberSliderInput(String valueId, double value, double min, double max, double step, boolean isInteger) {
        super(valueId);

        this.doubleValue.set(value);
        this.min.set(min);
        this.max.set(max);
        this.step.set(step);
        this.isInteger = isInteger;
        this.setValue(numberToString(value));
        this.doubleListener = (b, o, n) -> {
            if (syncing) {
                return;
            }
            this.value.set(numberToString(n));
        };

        slider = new Slider();
        slider.setBlockIncrement(this.step.getValue());
        slider.setSnapToTicks(true);
        slider.majorTickUnitProperty().bind(slider.blockIncrementProperty());
        slider.setMinorTickCount(0);

        slider.valueProperty().bindBidirectional(this.doubleValue);
        slider.minProperty().bindBidirectional(this.min);
        slider.maxProperty().bindBidirectional(this.max);
        slider.blockIncrementProperty().bindBidirectional(this.step);

        root = new Pane();
        expander = new NumberSliderExpander(slider, isInteger, this.doubleValue, this.min, this.max, this.step);
        expander.setLayoutX(0);
        expander.setLayoutY(0);
        slider.setLayoutX(30);
        slider.setLayoutY(4);
        root.getChildren().addAll(expander, slider);
        root.setOnMouseEntered(eh -> slider.requestFocus());

        this.doubleValue.addListener(doubleListener);
    }

    private String numberToString(Number number) {
        return isInteger ? String.valueOf(number.intValue()) : number.toString();
    }

    @Override
    public Node node() {
        return root;
    }

    @Override
    public void onValueChangedByApp(String newVal) {
        newVal = newVal == null ? "0" : newVal;
        var newNum = Double.parseDouble(newVal);
        var oldNum = doubleValue.getValue();
        if (Double.compare(newNum, oldNum) == 0) {
            return;
        }
        syncing = true;
        doubleValue.setValue(newNum);
        syncing = false;
    }

    @Override
    public void onDispose() {
        doubleValue.removeListener(doubleListener);
        slider.valueProperty().unbindBidirectional(doubleValue);
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

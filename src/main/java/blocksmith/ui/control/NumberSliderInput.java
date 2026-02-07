package blocksmith.ui.control;

import blocksmith.xml.v2.ValueXml;
import btslib.ui.NumberSliderExpander;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javax.xml.namespace.QName;

/**
 *
 * @author joost
 */
public abstract class NumberSliderInput extends InputControl<String> {

    private final ChangeListener<Number> fxListener = (b, o, n) -> onValueChangedByUser(n.toString());

    protected DoubleProperty value = new SimpleDoubleProperty();
    protected DoubleProperty min = new SimpleDoubleProperty();
    protected DoubleProperty max = new SimpleDoubleProperty();
    protected DoubleProperty step = new SimpleDoubleProperty();
    private final boolean isInteger;

    private final Pane root;
    private final Slider slider;
    private final NumberSliderExpander expander;

    public NumberSliderInput(double value, double min, double max, double step, boolean isInteger) {

        this.value.set(value);
        this.min.set(min);
        this.max.set(max);
        this.step.set(step);
        this.isInteger = isInteger;

//        slider = new Slider(0, 10, 0);
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
        if(isInteger) {
            return value.getValue().intValue() + "";
        }
        return value.getValue().toString();
    }

    @Override
    public void setValue(String newVal) {
        var newNum = Double.parseDouble(newVal);
        var oldNum = value.getValue().doubleValue();
        if (Double.compare(newNum, newNum) == 0) {
            return;
        }
        value.setValue(newNum);
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
    public Optional<ValueXml> serialize() {
        if (isEditable()) {
            var value = new ValueXml();
            value.setValue(getValue().toString());
            value.getOtherAttributes().put(new QName("min"), min.getValue().toString());
            value.getOtherAttributes().put(new QName("max"), max.getValue().toString());
            value.getOtherAttributes().put(new QName("step"), step.getValue().toString());
            return Optional.ofNullable(value);
        }
        return Optional.empty();
    }

    @Override
    protected void onEditableChanged(boolean isEditable) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public InputControl<String> copy() {
        var control = new DoubleSliderInput();
        if (isEditable()) {
            control.value.setValue(this.value.getValue());
            control.min.setValue(this.min.getValue());
            control.max.setValue(this.max.getValue());
            control.step.setValue(this.step.getValue());
        }
        return control;
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

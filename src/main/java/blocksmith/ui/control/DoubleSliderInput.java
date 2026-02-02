package blocksmith.ui.control;

import java.util.Objects;
import javafx.beans.property.SimpleDoubleProperty;

/**
 *
 * @author joost
 */
public class DoubleSliderInput extends NumberSliderInput {

    @Override
    protected void initializeProperties() {
        this.value = new SimpleDoubleProperty(0);
        this.min = new SimpleDoubleProperty(0);
        this.max = new SimpleDoubleProperty(10);
        this.step = new SimpleDoubleProperty(0.1);
    }

    @Override
    protected boolean isIntegerSlider() {
        return false;
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

}

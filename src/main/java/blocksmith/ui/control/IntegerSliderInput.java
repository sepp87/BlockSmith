package blocksmith.ui.control;

import javafx.beans.property.SimpleIntegerProperty;

/**
 *
 * @author joost
 */
public class IntegerSliderInput extends NumberSliderInput {

    @Override
    protected void initializeProperties() {
        this.value = new SimpleIntegerProperty(0);
        this.min = new SimpleIntegerProperty(0);
        this.max = new SimpleIntegerProperty(10);
        this.step = new SimpleIntegerProperty(1);
    }

    @Override
    protected boolean isIntegerSlider() {
        return true;
    }

    @Override
    public void setValue(String newVal) {
        var newNum = Integer.parseInt(newVal);
        var oldNum = value.getValue().intValue();
        if(Integer.compare(oldNum, newNum) == 0) {
            return;
        }
        value.setValue(newNum);
    }

    @Override
    public void setEditable(boolean isEditable) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}

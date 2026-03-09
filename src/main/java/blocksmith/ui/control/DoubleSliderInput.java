package blocksmith.ui.control;

import java.util.Objects;
import javafx.beans.property.SimpleDoubleProperty;

/**
 *
 * @author joost
 */
public class DoubleSliderInput extends NumberSliderInput {

    public DoubleSliderInput(String valueId) {
        super(valueId, 0, 0, 10, .1, false);
    }

}

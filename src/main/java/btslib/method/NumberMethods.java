package btslib.method;

import blocksmith.domain.value.ParamInput.Range;
import blocksmith.infra.blockloader.annotations.Value;
import blocksmith.infra.blockloader.annotations.Block;

/**
 *
 * @author joost
 */
public class NumberMethods {

    @Block(
            type = "Input.integerSlider",
            aliases = {"Number.integer"},
            name = "Integer",
            description = "Integer slider",
            category = "Core")
    public static int inputInteger(@Value(input = Range.class) String value) {
        var number = Integer.parseInt(value);
        return number;
    }

    @Block(
            type = "Input.doubleSlider",
            aliases = {"Number.double"},
            name = "Double",
            description = "Double slider",
            category = "Core")
    public static double inputDouble(@Value(input = Range.class) String value) {
        var number = Double.parseDouble(value);
        return number;
    }

}

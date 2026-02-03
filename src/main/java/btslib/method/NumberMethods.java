package btslib.method;

import blocksmith.domain.block.ParamInput.Range;
import btscore.graph.block.BlockMetadata;
import blocksmith.domain.block.Value;

/**
 *
 * @author joost
 */
public class NumberMethods {

    @BlockMetadata(
            label = "Integer",
            description = "Integer slider",
            type = "Number.integer",
            category = "Core")
    public static int inputInteger(@Value(input = Range.class) String value) {
        var number = Integer.parseInt(value);
        return number;
    }

    @BlockMetadata(
            label = "Double",
            description = "Double slider",
            type = "Number.double",
            category = "Core")
    public static double inputDouble(@Value(input = Range.class) String value) {
        var number = Double.parseDouble(value);
        return number;
    }

}

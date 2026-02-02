package btslib.method;

import blocksmith.domain.block.Param;
import blocksmith.domain.block.ParamInput.Range;
import btscore.graph.block.BlockMetadata;

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
    public static int inputInteger(@Param(input = Range.class) String value) {
        var number = Integer.parseInt(value);
        return number;
    }

    @BlockMetadata(
            label = "Double",
            description = "Double slider",
            type = "Number.double",
            category = "Core")
    public static double inputDouble(@Param(input = Range.class) String value) {
        var number = Double.parseDouble(value);
        return number;
    }

}

package btslib.method;

import blocksmith.domain.block.Param;
import btscore.graph.block.BlockMetadata;

/**
 *
 * @author joost
 */
public class BooleanMethods {

    @BlockMetadata(
            type = "Boolean.new", // Input.boolean
            category = "Input",
            description = "Switch between TRUE and FALSE",
            tags = {"boolean", "true", "false"})
    public static boolean inputBoolean(@Param boolean value) {
        return value;
    }

    @BlockMetadata(
            type = "Boolean.negate",
            category = "Input",
            description = "Negate the value of a boolean e.g. from TRUE to FALSE",
            tags = {"boolean", "true", "false"})
    public static boolean negateBoolean(boolean value) {
        return !value;
    }
}

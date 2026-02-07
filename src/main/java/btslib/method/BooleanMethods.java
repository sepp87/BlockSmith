package btslib.method;

import btscore.graph.block.BlockMetadata;
import blocksmith.domain.block.Value;

/**
 *
 * @author joost
 */
public class BooleanMethods {

    @BlockMetadata(
            type = "Input.boolean", // Input.boolean
            aliases = {"Boolean.new"},
            category = "Input",
            description = "Switch between TRUE and FALSE",
            tags = {"boolean", "true", "false"})
    public static boolean inputBoolean(@Value boolean value) {
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

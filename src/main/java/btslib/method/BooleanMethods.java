package btslib.method;

import blocksmith.infra.blockloader.annotations.Value;
import blocksmith.infra.blockloader.annotations.Block;

/**
 *
 * @author joost
 */
public class BooleanMethods {

    @Block(
            type = "Input.boolean", // Input.boolean
            aliases = {"Boolean.new"},
            category = "Input",
            description = "Switch between TRUE and FALSE",
            tags = {"boolean", "true", "false"})
    public static boolean inputBoolean(@Value boolean value) {
        return value;
    }

    @Block(
            type = "Boolean.negate",
            category = "Input",
            description = "Negate the value of a boolean e.g. from TRUE to FALSE",
            tags = {"boolean", "true", "false"})
    public static boolean negateBoolean(boolean value) {
        return !value;
    }
}

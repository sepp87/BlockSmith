package blocksmith.lib;

import blocksmith.infra.blockloader.annotations.Block;
import blocksmith.infra.blockloader.annotations.Display;

/**
 *
 * @author joostmeulenkamp
 */
public class ObjectMethods {

    @Block(
            type = "Object.getClass",
            aliases = {"Object.getType"},
            category = "Core",
            description = "Returns the runtime class of this Object.")
    public static Class<?> getClass(Object object) {
        return object.getClass();
    }

    @Block(
            type = "Object.toString",
            aliases = {"String.fromObject"},
            category = "Core",
            description = "Returns a string representation of the object.")
    public static String toString(Object object) {
        return object.toString();
    }

    @Block(
            type = "Object.inspect",
            category = "Core",
            description = "Inspect the incoming value as plain text.")
    public static <T> T inspect(@Display T object) {
        return object;
    }
    
}

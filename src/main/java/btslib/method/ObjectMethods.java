package btslib.method;

import blocksmith.infra.blockloader.annotations.Block;

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

}

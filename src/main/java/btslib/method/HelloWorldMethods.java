package btslib.method;

import blocksmith.infra.blockloader.annotations.Block;
import blocksmith.infra.blockloader.annotations.Value;

/**
 *
 * @author joost
 */
public class HelloWorldMethods {

    @Block(
            type = "Hello.World",
            category = "General",
            description = "A template block for further customization",
            tags = {"template", "dummy", "example"})
    public static String helloWorld() {
        return "Hello world!";
    }

    @Block(
            type = "Hello.You",
            category = "General",
            description = "A template block for further customization",
            tags = {"template", "dummy", "example"})
    public static String helloYou(@Value String name) {
        return "Hello " + name + "!";
    }
}

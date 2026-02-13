package btslib.method;

import blocksmith.domain.value.ParamInput;
import javafx.scene.paint.Color;
import blocksmith.infra.blockloader.annotations.Value;
import blocksmith.infra.blockloader.annotations.Block;

/**
 *
 * @author joost
 */
public class PaintMethods {

    @Block(
            type = "Input.color",
            aliases = {"Paint.color", "Color.new"},
            name = "Color",
            description = "Pick a nice color from the palette",
            category = "Core")
    public static Color inputColor(@Value(input = ParamInput.Color.class) String value) {
        var color = Color.valueOf(value);
        return color;
    }

}

package btslib.method;

import blocksmith.domain.block.ParamInput;
import btscore.graph.block.BlockMetadata;
import javafx.scene.paint.Color;
import blocksmith.domain.block.Value;

/**
 *
 * @author joost
 */
public class PaintMethods {

    @BlockMetadata(
            label = "Color",
            description = "Pick a nice color from the palette",
            type = "Paint.color",
            category = "Core")
    public static Color inputColor(@Value(input = ParamInput.Color.class) String value) {
        var color = Color.valueOf(value);
        return color;
    }

}

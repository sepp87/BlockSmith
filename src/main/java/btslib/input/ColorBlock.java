package btslib.input;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javax.xml.namespace.QName;
import btslib.ui.ColorBox;
import btsxml.BlockTag;
import btscore.graph.block.BlockMetadata;
import btscore.graph.block.BlockModel;
import btscore.workspace.WorkspaceModel;

/**
 *
 * @author JoostMeulenkamp
 */
@BlockMetadata(
        identifier = "Input.color",
        category = "Input",
        description = "Pick a nice color from the palette",
        tags = {"input", "color"})
public class ColorBlock extends BlockModel {

    private final ObjectProperty<Color> color = new SimpleObjectProperty<>(Color.WHITE);
    private ColorBox picker;

    public ColorBlock() {
//    public ColorBlock(WorkspaceModel workspaceModel) {
//        super(workspaceModel);
        this.nameProperty().set("Color Picker");
        addOutputPort("color", Color.class);
        initialize();
    }

    @Override
    protected final void initialize() {
        outputPorts.get(0).dataProperty().bind(color);
    }

    @Override
    public Region getCustomization() {
        picker = new ColorBox();
        picker.customColorProperty().bindBidirectional(color);
        return picker;
    }

    public ObjectProperty<Color> colorProperty() {
        return color;
    }

    @Override
    public void process() {
    }

    @Override
    public void serialize(BlockTag xmlTag) {
        super.serialize(xmlTag);
        xmlTag.getOtherAttributes().put(QName.valueOf("color"), color.get().toString());
    }

    @Override
    public void deserialize(BlockTag xmlTag) {
        super.deserialize(xmlTag);
        String value = xmlTag.getOtherAttributes().get(QName.valueOf("color"));
        color.set(Color.valueOf(value));
    }

    @Override
    public BlockModel copy() {
        ColorBlock block = new ColorBlock();
//        ColorBlock block = new ColorBlock(workspace);
        block.color.set(this.color.get());
        return block;
    }

    @Override
    public void onRemoved() {
        outputPorts.get(0).dataProperty().unbind();
        if (picker != null) {
            picker.customColorProperty().unbindBidirectional(color);
            picker.remove();
        }
    }

}

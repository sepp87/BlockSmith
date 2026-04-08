package blocksmith.ui.control;

import blocksmith.ui.graph.block.BlockView;
import blocksmith.utils.icons.FontAwesomeSolid;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;

/**
 *
 * @author joost
 */
public class BooleanInput extends InputControl<Boolean> {

    private final BooleanProperty booleanValue = new SimpleBooleanProperty(false);

    private final Label onOffSwitch;
    private final String offIcon;
    private final String onIcon;

    public BooleanInput(String valueId) {
        super(valueId);

        onOffSwitch = BlockView.getAwesomeIcon(FontAwesomeSolid.TOGGLE_OFF);
        offIcon = onOffSwitch.getText();
        onIcon = FontAwesomeSolid.TOGGLE_ON.unicode();
        onOffSwitch.setOnMouseClicked(eh -> toggleValue());
    }

    private void toggleValue() {
        var isOn = !booleanValue.get();
        setBooleanValue(isOn);
    }

    private void setBooleanValue(boolean isOn) {
        booleanValue.set(isOn);
        value.setValue(String.valueOf(isOn));
        String icon = isOn ? onIcon : offIcon;
        onOffSwitch.textProperty().set(icon);
    }

    @Override
    public Node node() {
        return onOffSwitch;
    }

    @Override
    protected void onValueChangedByApp(String newVal) {
        var parsed = Boolean.valueOf(newVal);
        setBooleanValue(parsed);
    }

    @Override
    public void onDispose() {
        onOffSwitch.setOnMouseClicked(null);
    }

}

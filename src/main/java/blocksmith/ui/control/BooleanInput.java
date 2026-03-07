package blocksmith.ui.control;

import blocksmith.ui.graph.block.BlockView;
import blocksmith.ui.icons.FontAwesomeSolid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Label;

/**
 *
 * @author joost
 */
public class BooleanInput extends InputControl<Boolean> {

    private final ChangeListener<Boolean> fxListener = (b, o, n) -> onValueChangedByUser(n.toString());
    private final BooleanProperty value = new SimpleBooleanProperty(false);

    private Label onOffSwitch;
    private String offIcon;
    private String onIcon;

    public BooleanInput() {
        onOffSwitch = BlockView.getAwesomeIcon(FontAwesomeSolid.TOGGLE_OFF);
        offIcon = onOffSwitch.getText();
        onIcon = FontAwesomeSolid.TOGGLE_ON.unicode();
        value.addListener(fxListener);
        onOffSwitch.setOnMouseClicked(eh -> toggleValue());
    }

    private void toggleValue() {
        var isOn = !value.get();
        value.set(isOn);
        String icon = isOn ? onIcon : offIcon;
        onOffSwitch.textProperty().set(icon);
    }

    @Override
    public Node node() {
        return onOffSwitch;
    }

    @Override
    public String getValue() {
        return value.getValue().toString();
    }

    @Override
    public void setValue(String newVal) {
        if (newVal == null || Objects.equals(value.get(), newVal)) {
            return;
        }
        
        var parsed = Boolean.valueOf(newVal);
        if (Objects.equals(value.get(), parsed)) {
            return;
        }

        value.set(parsed);

    }

    @Override
    public void dispose() {
        value.removeListener(fxListener);
        onOffSwitch.setOnMouseClicked(null);
    }

    @Override
    protected void onEditableChanged(boolean isEditable) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public InputControl<Boolean> copy() {
        var control = new BooleanInput();
        if (isEditable()) {
            control.setValue(this.getValue());
        }
        return control;
    }

}

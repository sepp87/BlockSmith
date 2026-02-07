package blocksmith.ui.control;

import btscore.graph.base.BaseButton;
import btscore.icons.FontAwesomeSolid;
import java.io.File;
import java.util.Objects;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/**
 *
 * @author joost
 */
public abstract class AbstractPathInput extends InputControl<String> {

    private final ChangeListener<String> fxListener = (b, o, n) -> onValueChangedByUser(n);
    private final StringProperty value = new SimpleStringProperty();

    private HBox root;
    protected TextField textField;
    protected BaseButton button;

    public AbstractPathInput() {
        root = new HBox(5);

        textField = new TextField();
        textField.setFocusTraversable(false);
        textField.textProperty().bindBidirectional(value);

        button = new BaseButton(FontAwesomeSolid.FOLDER_OPEN);
        button.setOnAction(ah -> {
            var file = choosePath();
            value.set(file == null ? null : file.getPath());
        });

        root.getChildren().addAll(textField, button);
        root.setOnMouseEntered(eh -> textField.requestFocus());

        value.addListener(fxListener);

    }

    abstract protected File choosePath();

    @Override
    public Node node() {
        return root;
    }

    @Override
    public String getValue() {
        return value.get();
    }

    @Override
    public void setValue(String newVal) {
        if (Objects.equals(value.get(), newVal)) {
            return;
        }
        value.set(newVal);
    }

    @Override
    public void dispose() {
        textField.textProperty().unbindBidirectional(value);
        button.setOnAction(null);
        root.setOnMouseEntered(null);
        value.removeListener(fxListener);
    }

}

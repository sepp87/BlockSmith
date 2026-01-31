package blocksmith.ui.control;

import btscore.graph.base.BaseButton;
import btscore.icons.FontAwesomeSolid;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

/**
 *
 * @author joost
 */
public class FilePathInput implements InputControl<String> {

    protected final List<Consumer<String>> listeners = new ArrayList<>();
    protected final ChangeListener<String> fxListener = (b, o, n) -> listeners.forEach(c -> c.accept(n));
    protected final StringProperty value = new SimpleStringProperty();

    private HBox root;
    private TextField textField;
    protected BaseButton button;

    public FilePathInput() {
        root = new HBox(5);

        textField = new TextField();
        textField.setFocusTraversable(false);
        textField.textProperty().bindBidirectional(value);
        setPromptText("Open a file...");

        button = new BaseButton(FontAwesomeSolid.FOLDER_OPEN);
        button.setOnAction(this::handleOpenFile);

        root.getChildren().addAll(textField, button);
        root.setOnMouseEntered(eh -> textField.requestFocus());

        value.addListener(fxListener);

    }

    protected void setPromptText(String text) {
        textField.setPromptText(text);
    }

    private void handleOpenFile(ActionEvent event) {
        var file = openFile();
        value.set(file == null ? null : file.getPath());
    }

    protected File openFile() {
        var picker = new FileChooser();
        picker.setTitle("Choose a file...");
        var window = button.getScene().getWindow();
        var file = picker.showOpenDialog(window);
        return file;
    }

    @Override
    public Node node() {
        return root;
    }

    @Override
    public String getValue() {
        return value.get();
    }

    @Override
    public void setValue(String value) {
        if (Objects.equals(this.value.get(), value)) {
            return;
        }
        this.value.set(value);
    }

    @Override
    public void dispose() {
        textField.textProperty().unbindBidirectional(value);
        button.setOnAction(null);
        root.setOnMouseEntered(null);
        value.removeListener(fxListener);
    }

    @Override
    public void setOnValueChanged(Consumer<String> listener) {
        listeners.add(listener);
    }

}

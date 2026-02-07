package blocksmith.ui.control;

import btscore.graph.base.BaseButton;
import btscore.icons.FontAwesomeSolid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/**
 *
 * @author joost
 */
public class PasswordInput extends InputControl<String> {

    private final ChangeListener<String> fxListener = (b, o, n) -> onValueChangedByUser(n);

    private final BooleanProperty hidden = new SimpleBooleanProperty(true);
    private final StringProperty value = new SimpleStringProperty();

    private final HBox root;
    private final PasswordField passwordField;
    private final TextField textField;
    private final BaseButton toggleButton;

    public PasswordInput() {
        passwordField = new PasswordField();
        configureField(passwordField, hidden.not());

        textField = new TextField();
        configureField(textField, hidden);
//        textField.setManaged(false); // Initially hide the text field
//        textField.setVisible(false);

        toggleButton = new BaseButton(FontAwesomeSolid.EYE_SLASH);
        toggleButton.setOnAction(this::toggleHidden);

        root = new HBox(10, passwordField, textField, toggleButton);

        value.addListener(fxListener);

    }

    private void configureField(TextField field, ObservableBooleanValue isHidden) {
        field.managedProperty().bind(isHidden); // Bind visibility and management based on hidden property
        field.visibleProperty().bind(isHidden);
        field.setPromptText("Write here...");
        field.setFocusTraversable(false);
        field.setMinWidth(100);
        field.setStyle("-fx-pref-column-count: 26;");
        field.textProperty().bindBidirectional(value); // Sync content between password field and text field
        field.setOnMouseEntered(eh -> field.requestFocus());
    }

    private void toggleHidden(ActionEvent event) {
        boolean isHidden = hidden.get();
        hidden.set(!isHidden);
        FontAwesomeSolid icon = !isHidden ? FontAwesomeSolid.EYE_SLASH : FontAwesomeSolid.EYE;
        toggleButton.setText(icon);
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
    public void setValue(String newVal) {
        if (Objects.equals(textField.getText(), newVal)) {
            return;
        }
        value.set(newVal);
    }

    @Override
    public void dispose() {
        value.removeListener(fxListener);

        disposeField(passwordField);
        disposeField(textField);

        toggleButton.setOnAction(null);
    }

    private void disposeField(TextField field) {
        field.managedProperty().unbind();
        field.visibleProperty().unbind();
        field.textProperty().unbindBidirectional(value);
        field.setOnMouseEntered(null);
    }

    @Override
    protected void onEditableChanged(boolean isEditable) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }


    @Override
    public InputControl<String> copy() {
        var control = new PasswordInput();
        if(isEditable()) {
            control.setValue(this.getValue());
        }
        return control;
    }

}

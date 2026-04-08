package blocksmith.ui.control;

import blocksmith.ui.graph.base.BaseButton;
import blocksmith.utils.icons.FontAwesomeSolid;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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

    private final BooleanProperty hidden = new SimpleBooleanProperty(true);

    private final HBox root;
    private final PasswordField passwordField;
    private final TextField textField;
    private final BaseButton toggleButton;

    public PasswordInput(String valueId) {
        super(valueId);
        
        passwordField = new PasswordField();
        configureField(passwordField, hidden.not());

        textField = new TextField();
        configureField(textField, hidden);
        
        toggleButton = new BaseButton(FontAwesomeSolid.EYE_SLASH);
        toggleButton.setOnAction(this::toggleHidden);

        root = new HBox(10, passwordField, textField, toggleButton);

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
    public void onValueChangedByApp(String newVal) {
    }

    @Override
    public void onDispose() {
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

}

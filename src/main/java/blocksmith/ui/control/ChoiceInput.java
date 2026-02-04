package blocksmith.ui.control;

import btscore.utils.ListViewHoverSelectBehaviour;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.layout.VBox;

/**
 *
 * @author joost
 */
public class ChoiceInput implements InputControl<String> {

    private final List<Consumer<String>> listeners = new ArrayList<>();
    private final ChangeListener<String> fxListener = (b, o, n) -> listeners.forEach(c -> c.accept(n));

    private final Set<String> options = new TreeSet<>();
    private final StringProperty value = new SimpleStringProperty();

    private final VBox root;
    private final ComboBox<String> comboBox;
    private ListView<String> listView;

    public ChoiceInput(String selected, Collection<String> options) {

        comboBox = new ComboBox<>();
        comboBox.getItems().addAll(options);
        comboBox.valueProperty().bindBidirectional(this.value);
        comboBox.setPromptText("Select a value");
        comboBox.setPrefWidth(202);
        comboBox.setMaxWidth(202);

        comboBox.setOnShown(event -> {
            ComboBoxListViewSkin<String> skin = (ComboBoxListViewSkin<String>) comboBox.getSkin();
            this.listView = (ListView<String>) skin.getPopupContent();
            new ListViewHoverSelectBehaviour(listView);
        });

        root = new VBox(comboBox);
        root.setOnMouseEntered(eh -> comboBox.requestFocus());

        setValue(selected);
        value.addListener(fxListener);

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
        if (Objects.equals(value.get(), newVal)) {
            return;
        }
        value.set(newVal);
    }

    @Override
    public void dispose() {
        root.setOnMouseEntered(null);
        value.unbindBidirectional(comboBox.valueProperty());
        value.removeListener(fxListener);
    }

    @Override
    public void setOnValueChanged(Consumer<String> listener) {
        listeners.add(listener);
    }

    @Override
    public void setEditable(boolean isEditable) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @Override
    public InputControl<String> copy() {
        if (isEditable()) {
            var selected = this.getValue();
            return new ChoiceInput(selected, options);
        } else {
            return new ChoiceInput(options.iterator().next(), options);
        }
    }

}

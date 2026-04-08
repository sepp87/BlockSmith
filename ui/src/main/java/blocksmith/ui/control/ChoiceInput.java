package blocksmith.ui.control;

import blocksmith.ui.utils.ListViewHoverSelectBehaviour;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.layout.VBox;

/**
 *
 * @author joost
 */
public class ChoiceInput extends InputControl<String> {


    private final Set<String> options = new TreeSet<>();

    private final VBox root;
    private final ComboBox<String> comboBox;
    private ListView<String> listView;

    public ChoiceInput(String valueId, String selected, Collection<String> options) {
        super(valueId);

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

    }

    @Override
    public Node node() {
        return root;
    }

    @Override
    protected void onValueChangedByApp(String newVal) {
    }

    @Override
    public void onDispose() {
        root.setOnMouseEntered(null);
        value.unbindBidirectional(comboBox.valueProperty());
    }

}
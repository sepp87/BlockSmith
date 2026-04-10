package blocksmith.ui.graph.block;

import blocksmith.ui.control.InputControl;
import blocksmith.domain.block.BlockDef;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.block.BlockLayout;
import blocksmith.domain.connection.PortRef;
import blocksmith.exec.ExecutionState;
import blocksmith.ui.control.MultilineTextInput;
import blocksmith.ui.display.ValueInspector;
import blocksmith.ui.display.ValueDisplay;
import blocksmith.utils.icons.FontAwesomeSolid;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 *
 * @author JoostMeulenkamp
 */
@blocksmith.infra.blockloader.annotations.Block(
        type = "Core.methodBlock",
        category = "Core",
        description = "A generic block used to convert static methods and fields to blocks",
        tags = {"core", "method", "block"})
public class MethodBlockNew extends BlockModel {

    private final BlockDef def;
    private final Map<String, InputControl<?>> inputControls = new HashMap<>();
    private final List<ValueInspector> valueInspectors = new ArrayList<>();
    private Pane container;
    private ProgressIndicator spinner;
    private Label label;

    public MethodBlockNew(BlockDef def) {
        this(def,  null);
    }

    public MethodBlockNew(BlockDef def,  String id) {
        if (id != null) {
            this.id.set(id);
        }
        this.def = def;
    }

    public void updateLayoutFrom(BlockLayout update) {
        this.labelProperty().set(update.label());
        this.layoutXProperty().set(update.x());
        this.layoutYProperty().set(update.y());
        if (resizableProperty().get()) {
            this.widthProperty().set(update.width());
            this.heightProperty().set(update.height());
        }
    }

    public void addInputControl(String valueId, InputControl control) {
        inputControls.put(valueId, control);
        if (control instanceof MultilineTextInput) {
            resizableProperty().set(true);
        }
    }

    public void addValueDisplay(PortRef ref, ValueDisplay display) {
        var inspector = new ValueInspector(ref, display);
        valueInspectors.add(inspector);
        resizableProperty().set(true);
    }

    public Map<String, InputControl<?>> getInputControls() {
        return inputControls;
    }

    @Override
    public Region getCustomization() {

        if (!inputControls.isEmpty()) {
            VBox localContainer = new VBox();
            var controls = inputControls.values().stream().map(InputControl::node).toList();
            localContainer.getChildren().addAll(controls);
            if (controls.size() == 1) {
                VBox.setVgrow(controls.get(0), Priority.ALWAYS);
            }
            container = localContainer;

        } else if (!valueInspectors.isEmpty()) {
            VBox localContainer = new VBox();
            var displays = valueInspectors.stream().map(ValueInspector::node).toList();
            localContainer.getChildren().addAll(displays);
            if (displays.size() == 1) {
                VBox.setVgrow(displays.get(0), Priority.ALWAYS);
            }
            container = localContainer;

        } else {
            spinner = new ProgressIndicator();

            if (!def.icon().equals(FontAwesomeSolid.NOT_SPECIFIED)) {
                label = BlockView.getAwesomeIcon(def.icon());

            } else if (!def.name().equals("")) {
                label = new Label(def.name());
                label.getStyleClass().add("block-text");

            } else {
                String shortName = def.type().split("\\.")[1];
                label = new Label(shortName);
                label.getStyleClass().add("block-text");
            }
            spinner.prefWidthProperty().bind(label.widthProperty());
            container = new StackPane(label);
        }
        return container;
    }


    public void updateFrom(ExecutionState runtime) {
        var block = BlockId.from(getId());
        var status = runtime.statusOf(block);
        switch (status) {
            case RUNNING: // set spinner
                if (spinner != null && label.getWidth() != 0.0) {
                    spinner.setMinWidth(label.getWidth());
                    container.getChildren().clear();
                    container.getChildren().add(spinner);
                }
                break;
            default: // remove spinner
                if (spinner != null && label.getWidth() != 0.0) {
                    container.getChildren().clear();
                    container.getChildren().add(label);
                }
                break;
        }

        var values = runtime.valuesOf(block);
        for (var val : values.entrySet()) {
            var ref = val.getKey();
//                System.out.println("METHODBLOCK " + GraphLogFmt.port(ref) + " = " + String.valueOf(val.getValue()));
        }

        var errors = runtime.exceptionsOf(block);

        for (var inspector : valueInspectors) {
            var value = values.get(inspector.ref());
            inspector.setData(value);
        }

        int size = exceptions.size();
        exceptions.addAll(errors);
        exceptions.remove(0, size);
//            if (!inputPorts.isEmpty() && inputPorts.stream().noneMatch(PortModel::isActive)) {
//                exceptions.clear();
//            }

    }

    @Override
    public String type() {
        return def.type();
    }

    @Override
    public String description() {
        return def.description();
    }

    @Override
    protected void onRemoved() {
        if (container != null) {
            container.prefWidthProperty().unbind();
        }
    }

    enum LacingMode {
        SHORTEST,
        LONGEST,
        CROSS_PRODUCT
    }

}

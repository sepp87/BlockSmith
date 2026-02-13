package blocksmith.ui;

import blocksmith.ui.control.InputControl;
import blocksmith.domain.block.BlockDef;
import blocksmith.domain.value.ValueType;
import blocksmith.exec.BlockExecutor;
import blocksmith.exec.BlockFunc;
import blocksmith.ui.control.MultilineTextInput;
import blocksmith.ui.control.NumberSliderInput;
import blocksmith.xml.v2.ValueXml;
import btscore.graph.block.BlockModel;
import btscore.graph.block.BlockView;
import btscore.icons.FontAwesomeSolid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import btsxml.BlockTag;
import btscore.graph.port.PortModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javax.xml.namespace.QName;
import blocksmith.infra.blockloader.annotations.Block;

/**
 *
 * @author JoostMeulenkamp
 */
@Block(
        type = "Core.methodBlock",
        category = "Core",
        description = "A generic block used to convert static methods and fields to blocks",
        tags = {"core", "method", "block"})
public class MethodBlockNew extends BlockModel {

    private final BlockDef def;
    private final BlockFunc func;
    private final Map<String, InputControl<?>> inputControls = new HashMap<>();
    private Pane container;
    private ProgressIndicator spinner;
    private Label label;

    public MethodBlockNew(BlockDef def, BlockFunc func) {
        this(def, func, null);
    }

    public MethodBlockNew(BlockDef def, BlockFunc func, String id) {
        if (id != null) {
            this.id.set(id);
        }
        this.def = def;
        this.func = func;
    }

    public void addInputControl(String name, InputControl control) {
        inputControls.put(name, control);
        if (control instanceof MultilineTextInput) {
            resizableProperty().set(true);
        }
        control.setOnValueChangedByUser(value -> processSafely());
    }

    public BlockDef getBlockDef() {
        return def;
    }

    public Map<String, InputControl<?>> getInputControls() {
        return inputControls;
    }

    @Override
    protected void initialize() {
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

        } else {
            spinner = new ProgressIndicator();

            if (!def.icon().equals(FontAwesomeSolid.NULL)) {
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

    @Override
    public void onIncomingConnectionAdded(Object data) {
        super.onIncomingConnectionAdded(data);

        if (inputControls.isEmpty()) {
            return;
        }

        for (var port : inputPorts) {
            if (!port.isConnected()) {
                continue;
            }
            String key = port.nameProperty().get();
            var control = inputControls.get(key);
            if (control == null) {
                continue;
            }
            control.setEditable(false);
//            System.out.println("MethodBlockNew.onIncomingConnectionAdded");

//            if (control instanceof MultilineTextInput textInput) {
//                textInput.setValue(data);
//            }
        }
    }

    @Override
    public void onIncomingConnectionRemoved(Object data) {
        super.onIncomingConnectionRemoved(data);

        if (inputControls.isEmpty()) {
            return;
        }

        for (var port : inputPorts) {
            if (port.isConnected()) {
                continue;
            }
            String key = port.nameProperty().get();
            var control = inputControls.get(key);
            if (control == null) {
                continue;
            }
            control.setEditable(true);

        }
    }

    public boolean isListOperator = false;
    public boolean isListWithUnknownReturnType = false;

    @Override
    public void processSafely() {

//        System.out.println(def.metadata().type().split("\\.")[1] + ".processSafely()");
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                process();
                return null;
            }
        };

        if (spinner != null && label.getWidth() != 0.0) {
            task.setOnSucceeded(event -> {
                container.getChildren().clear();
                container.getChildren().add(label);
            });
        }

        if (spinner != null && label.getWidth() != 0.0) {
            spinner.setMinWidth(label.getWidth());
            container.getChildren().clear();
            container.getChildren().add(spinner);
        }

        // Run the task in a separate thread
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

    }

    @Override
    public void process() {

        var inputData = inputPorts.stream().map(PortModel::getData).toArray();
        var controlData = inputControls.values().stream().map(InputControl::getValue).toArray();

        if (!inputControls.isEmpty()) {
            for (var input : inputPorts) {
                var valueId = input.nameProperty().get();
                var control = inputControls.get(valueId);
                if (control == null) {
                    continue;
                }
                var value = input.getData();
                if (control instanceof MultilineTextInput multiline && !multiline.isEditable()) {
//                            System.out.println("MethodBlockNew.process " + value);

                    multiline.setValue(value);
                }
            }
        }

        var parameters = controlData.length != 0 ? controlData : inputData; // TODO refactor as soon as inputs and controls start to mix

        var result = new BlockExecutor(def, func, isListOperator).invoke(parameters);

        if (isListWithUnknownReturnType && result.data().get() != null) {
            List<?> list = (List<?>) result.data().get();
            determineOutPortDataTypeFromList(list);
        }

        Platform.runLater(() -> {
            int size = exceptions.size();
            exceptions.addAll(result.exceptions());
            exceptions.remove(0, size);
            if (!inputPorts.isEmpty() && inputPorts.stream().noneMatch(PortModel::isActive)) {
                exceptions.clear();
            }
            Object data = result.data().get();
            outputPorts.get(0).setData(data);
            if ((data != null) && !List.class.isAssignableFrom(data.getClass())) {
                outputPorts.get(0).dataTypeProperty().set(data.getClass());
            }

        });

    }

    private void determineOutPortDataTypeFromList(List<?> list) {
        Set<Class<?>> classes = new HashSet<>();
        for (Object i : list) {
            if (i != null) {
                classes.add(i.getClass());
            }
        }
        if (classes.size() == 1) {
            Platform.runLater(() -> {
                PortModel port = this.outputPorts.get(0);
                Class<?> type = classes.iterator().next();
                port.dataTypeProperty().set(type);
                port.nameProperty().set(type.getSimpleName());
            });
        }
    }

    @Override
    public void serialize(BlockTag xmlTag) {
        super.serialize(xmlTag);
        xmlTag.setType(def.type());
        var values = xmlTag.getOtherAttributes();

        for (var entry : inputControls.entrySet()) {
            var control = entry.getValue();

            if (!control.isEditable()) {
                continue;
            }
            var valueId = entry.getKey();
            var value = control.getValue().toString();
            values.put(QName.valueOf(valueId), value);

            if (control instanceof NumberSliderInput slider) {
                values.put(QName.valueOf("min"), slider.getValue());
                values.put(QName.valueOf("max"), slider.getValue());
                values.put(QName.valueOf("step"), slider.getValue());
            }
        }
    }

    @Override
    public void deserialize(BlockTag xmlTag) {
        super.deserialize(xmlTag);
    }

    public Collection<ValueXml> serializeValues() {
        var result = new ArrayList<ValueXml>();
        for (var entry : inputControls.entrySet()) {
            var optional = entry.getValue().serialize();
            if (optional.isEmpty()) {
                continue;
            }
            var value = optional.get();
            value.setBlock(this.getId());
            value.setId(entry.getKey());
            result.add(value);
        }
        return result;
    }

    public void deserializeValues(Collection<ValueXml> values) {
        for (var literal : values) {
            var control = inputControls.get(literal.getId());
            control.parseValue(literal.getValue());
            if (control instanceof NumberSliderInput slider) {
                slider.setMin(Double.parseDouble(literal.getOtherAttributes().get("min")));
                slider.setMax(Double.parseDouble(literal.getOtherAttributes().get("max")));
                slider.setStep(Double.parseDouble(literal.getOtherAttributes().get("step")));
            }
        }
    }

    @Override
    public BlockModel copy() {
        var block = new MethodBlockNew(def, func);

        for (var input : def.inputs()) {
            block.addInputPort(input.valueName(), ValueType.toDataType(input.valueType()));
        }

        for (var output : def.outputs()) {
            block.addInputPort(output.valueName(), ValueType.toDataType(output.valueType()));
        }

        for (var control : inputControls.entrySet()) {
            block.addInputControl(control.getKey(), control.getValue());
        }

        block.isListOperator = this.isListOperator;
        block.isListWithUnknownReturnType = this.isListWithUnknownReturnType;

        return block;
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

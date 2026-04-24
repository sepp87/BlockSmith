package blocksmith.ui.graph.block;

import blocksmith.domain.value.Port;
import blocksmith.domain.value.ValueType;
import blocksmith.exec.BlockException;
import blocksmith.ui.graph.base.BaseModel;
import java.util.List;
import blocksmith.ui.graph.port.PortModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import blocksmith.infra.blockloader.annotations.Block;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;

/**
 *
 * @author joostmeulenkamp
 */
public abstract class BlockModel extends BaseModel {

    protected final ObjectProperty<Bounds> measuredBounds = new SimpleObjectProperty<>();
    protected final ObservableList<PortModel> inputPorts = FXCollections.observableArrayList();
    protected final ObservableList<PortModel> outputPorts = FXCollections.observableArrayList();
    protected final ObservableList<PortModel> ports = FXCollections.observableArrayList();

    protected final ObservableList<BlockException> exceptions = FXCollections.observableArrayList();

    public BlockModel() {
    }

    public Bounds measuredBounds() {
        return measuredBounds.get();
    }

    void setMeasuredBounds(Bounds bounds) {
        measuredBounds.set(bounds);
    }

    public ObservableList<BlockException> getExceptions() {
        return exceptions;
    }

    public abstract Region getCustomization();

    public EventHandler<MouseEvent> onMouseEntered() {
        return null;
    }

    public List<PortModel> getPorts() {
        return List.copyOf(ports);
    }

    public List<PortModel> getInputPorts() {
        return ports.stream().filter(p -> p.getDirection() == Port.Direction.INPUT).toList();
    }

    public List<PortModel> getOutputPorts() {
        return ports.stream().filter(p -> p.getDirection() == Port.Direction.OUTPUT).toList();
    }

    public PortModel addInputPort(String valueId, String valueName, ValueType valueType) {
        PortModel port = new PortModel(valueId, valueName, Port.Direction.INPUT, valueType, this, false);
        inputPorts.add(port);
        ports.add(port);
        return port;
    }

    public PortModel addOutputPort(String valueId, String valueName, ValueType valueType) {
        PortModel port = new PortModel(valueId, valueName, Port.Direction.OUTPUT, valueType, this, true);
        outputPorts.add(port);
        ports.add(port);
        return port;
    }

    public void removeInputPort(String valueId) {
        var port = inputPorts.stream().filter(p -> p.valueId().equals(valueId)).findFirst().orElseThrow();
        inputPorts.remove(port);
        ports.remove(port);
        port.dispose();
    }

    @Override
    public void dispose() {
        // clean up routine for sub-classes
        onRemoved();
        ports.forEach(p -> p.dispose());
        super.dispose();
    }

    protected abstract void onRemoved();

    public String type() {
        Block metadata = this.getClass().getAnnotation(Block.class);
        return metadata.type();
    }

    public String description() {
        Block metadata = this.getClass().getAnnotation(Block.class);
        return metadata.description();
    }

}

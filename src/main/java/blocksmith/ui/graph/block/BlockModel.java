package blocksmith.ui.graph.block;

import blocksmith.domain.value.Port;
import blocksmith.domain.value.ValueType;
import blocksmith.exec.BlockException;
import blocksmith.ui.graph.base.BaseModel;
import java.util.ArrayList;
import java.util.List;
import blocksmith.ui.graph.port.PortModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import blocksmith.ui.graph.connection.ConnectionModel;
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
    protected final ObservableList<BlockException> exceptions = FXCollections.observableArrayList();

    public BlockModel() {
    }

    public Bounds measuredBounds() {
        return measuredBounds.get();
    }

    void setMeasuredBounds(Bounds bounds) {
        measuredBounds.set(bounds);
    }

    protected abstract void initialize();

    public ObservableList<BlockException> getExceptions() {
        return exceptions;
    }

    public List<ConnectionModel> getConnections() {
        List<ConnectionModel> result = new ArrayList<>();
        for (PortModel port : inputPorts) {
            result.addAll(port.getConnections());
        }
        for (PortModel port : outputPorts) {
            result.addAll(port.getConnections());
        }
        return result;
    }


    public void onIncomingConnectionAdded(Object data) {

    }


    public void onIncomingConnectionRemoved(Object data) {

    }

    public abstract Region getCustomization();

    public EventHandler<MouseEvent> onMouseEntered() {
        return null;
    }

    public ObservableList<PortModel> getInputPorts() {
        return FXCollections.unmodifiableObservableList(inputPorts);
    }

    public ObservableList<PortModel> getOutputPorts() {
        return FXCollections.unmodifiableObservableList(outputPorts);
    }

    public PortModel addInputPort(String valueId, String valueName, ValueType valueType, Class<?> type) {
        return addInputPort(valueId, valueName, valueType, type, false);
    }

    public PortModel addInputPort(String valueId, String valueName, ValueType valueType, Class<?> type, boolean isAutoConnectable) {
        PortModel port = new PortModel(valueId, valueName, Port.Direction.INPUT, valueType, type, this, false);
        port.autoConnectableProperty().set(isAutoConnectable);
        inputPorts.add(port);
        return port;
    }

    public PortModel addOutputPort(String valueId, String valueName, ValueType valueType, Class<?> type) {
        return addOutputPort(valueId, valueName, valueType, type, false);
    }

    public PortModel addOutputPort(String valueId, String valueName, ValueType valueType, Class<?> type, boolean isAutoConnectable) {
        PortModel port = new PortModel(valueId, valueName, Port.Direction.OUTPUT, valueType, type, this, true);
        port.autoConnectableProperty().set(isAutoConnectable);
        outputPorts.add(port);
        return port;
    }

    @Override
    public void dispose() {
        // clean up routine for sub-classes
        onRemoved();

        // remove listeners
        for (PortModel port : inputPorts) {
            port.dispose();
        }
        for (PortModel port : outputPorts) {
            port.dispose();
        }

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

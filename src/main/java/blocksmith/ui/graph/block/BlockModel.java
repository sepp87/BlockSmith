package blocksmith.ui.graph.block;

import blocksmith.domain.value.Port;
import blocksmith.domain.value.ValueType;
import blocksmith.exec.BlockException;
import blocksmith.exec.BlockException.Severity;
import blocksmith.ui.graph.base.BaseModel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import blocksmith.ui.graph.port.PortModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import btsxml.BlockTag;
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
        this.active.addListener(activeListener);
    }

    public Bounds measuredBounds() {
        return measuredBounds.get();
    }

    void setMeasuredBounds(Bounds bounds) {
        measuredBounds.set(bounds);
    }

    protected abstract void initialize();

    private final ChangeListener<Boolean> activeListener = (b, o, n) -> onActiveChanged();

    /**
     * This method is called when the active state changes. When this block is
     * activated processSafely() is called in two cases; if this block has a
     * default output or if there are incoming connections. In case this block
     * was deactivated all outputs are set to null. Override this change
     * listener to modify its behaviour accordingly. Call
     * super.onActiveChanged() last if you want to continue with the default
     * behaviour.
     */
    protected void onActiveChanged() {
        if (!this.isActive()) {
            for (PortModel output : outputPorts) {
                output.setData(null);
            }
            return;
        }
        if (inputPorts.isEmpty()) {
            processSafely();
        }
    }

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

    /**
     * Safeguard against null data to ensure errors are shown. If there was no
     * previous connection, the data was already null. Since the new incoming
     * data is also null, onInputDataChanged wasn't triggered. However, now that
     * the user is interacting with the block, we should clearly indicate that
     * input data is missing. Therefor, processSafely is triggered in case of
     * null.
     */
    public void onIncomingConnectionAdded(Object data) {
//        if (data == null) {
//            processSafely();
//        }
    }

    /**
     * Safeguard against null data to ensure obsolete errors are cleared. If the
     * removed data was already null, it remains null and onInputDataChanged
     * won’t be triggered. Since the user is actively removing the connection,
     * any related errors are no longer relevant. Therefor, processSafely is
     * triggered in case of null.
     */
    public void onIncomingConnectionRemoved(Object data) {
//        if (data == null) {
//            processSafely();
//        }
    }

    public abstract Region getCustomization();

    public EventHandler<MouseEvent> onMouseEntered() {
        return null;
    }

    public void processSafely() {
        System.out.println(this.getClass().getSimpleName() + ".processSafely()");

        Set<BlockException> previousExceptions = new HashSet<>(exceptions);

        // Ensure processing only happens when active
        if (!this.isActive()) {
            return;
        }

        try {
            process();
        } catch (Exception exception) {
            BlockException blockException = new BlockException(null, Severity.ERROR, exception);
            exceptions.add(blockException);
            Logger.getLogger(BlockModel.class.getName()).log(Level.SEVERE, null, exception);
        }

        // When there are no more incoming connections, all exceptions should be cleared, since there is nothing to process
        if (!inputPorts.isEmpty() && inputPorts.stream().noneMatch(PortModel::isActive)) {
            exceptions.clear();
            return;
        }

        if (!(this instanceof MethodBlockNew)) {
            exceptions.removeAll(previousExceptions);
        }

    }

    protected abstract void process() throws Exception;


    public ObservableList<PortModel> getInputPorts() {
        return FXCollections.unmodifiableObservableList(inputPorts);
    }

    public List<PortModel> getReceivingPorts() {
        return getWirelessPorts(inputPorts);
    }

    public ObservableList<PortModel> getOutputPorts() {
        return FXCollections.unmodifiableObservableList(outputPorts);
    }

    public List<PortModel> getTransmittingPorts() {
        return getWirelessPorts(outputPorts);
    }

    private List<PortModel> getWirelessPorts(List<PortModel> ports) {
        List<PortModel> result = new ArrayList<>();
        for (PortModel port : ports) {
            if (port.autoConnectableProperty().get()) {
                result.add(port);
            }
        }
        return result;
    }

    public PortModel addInputPort(String valueId, String valueName, ValueType valueType, Class<?> type) {
        return addInputPort(valueId, valueName, valueType, type, false);
    }

    public PortModel addInputPort(String valueId, String valueName, ValueType valueType, Class<?> type, boolean isAutoConnectable) {
        PortModel port = new PortModel(valueId, valueName, Port.Direction.INPUT, valueType, type, this, false);
        port.dataProperty().addListener(inputDataListener);
        port.autoConnectableProperty().set(isAutoConnectable);
        inputPorts.add(port);
        return port;
    }

    private final ChangeListener<Object> inputDataListener = this::onInputDataChanged;

    private void onInputDataChanged(ObservableValue b, Object o, Object n) {
        processSafely();
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

        // stop processing
        setActive(false);

        // remove listeners
        this.active.removeListener(activeListener);
        for (PortModel port : inputPorts) {
            port.dataProperty().removeListener(inputDataListener);
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

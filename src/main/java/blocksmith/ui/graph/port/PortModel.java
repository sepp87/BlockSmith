package blocksmith.ui.graph.port;

import blocksmith.app.logging.GraphLogFmt;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.PortRef;
import blocksmith.domain.value.Port;
import blocksmith.domain.value.Port.Direction;
import blocksmith.domain.value.ValueType;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import blocksmith.ui.graph.base.BaseModel;
import blocksmith.ui.graph.block.BlockModel;
import blocksmith.ui.graph.connection.ConnectionModel;
import blocksmith.ui.utils.ObjectUtils;
import blocksmith.domain.graph.TypeCastUtils;
import java.io.File;
import java.nio.file.Path;
import java.util.function.Function;
import javafx.beans.value.ChangeListener;

/**
 *
 * @author JoostMeulenkamp
 */
public class PortModel extends BaseModel {

    private final String valueId;
    private final String valueName;
    private final Direction direction;
    private ValueType valueType;

    private final BooleanProperty autoConnectable = new SimpleBooleanProperty(this, "autoConnectable", false);
    private final ObjectProperty<Object> data = new SimpleObjectProperty<>(this, "data", null);
    private final ObservableSet<ConnectionModel> connections = FXCollections.observableSet();
    private final ObjectProperty<Class<?>> dataType = new SimpleObjectProperty<>(this, "dataType", null);

    private final int index;
    private final boolean multiDockAllowed;
    private final BlockModel block;

    public PortModel(
            String valueId,
            String valueName,
            Direction direction,
            ValueType valueType,
            Class<?> type,
            BlockModel block,
            boolean multiDockAllowed) {

        this.valueId = valueId;
        this.valueName = valueName;
        this.direction = direction;
        this.valueType = valueType;

        this.labelProperty().set(valueName + " : " + GraphLogFmt.valueType(valueType));
        this.index = (direction == Port.Direction.INPUT) ? block.getInputPorts().size() : block.getOutputPorts().size();
        this.block = block;
        this.multiDockAllowed = multiDockAllowed;
        this.dataType.set(type);

        data.addListener(dataListener);
    }

    public String valueId() {
        return valueId;
    }

    public void updateValueType(ValueType valueType) {
        this.valueType = valueType;
        labelProperty().set(valueName + " : " + GraphLogFmt.valueType(valueType));
    }

    @Override
    public void setActive(boolean isActive) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("PortModel controls its own active state.");
    }

    public BooleanProperty autoConnectableProperty() {
        return autoConnectable;
    }

    public Port.Direction getDirection() {
        return direction;
    }

    public int getIndex() {
        return index;
    }

    public boolean isMultiDockAllowed() {
        return multiDockAllowed;
    }

    public BlockModel getBlock() {
        return block;
    }

    public ObjectProperty<Class<?>> dataTypeProperty() {
        return dataType;
    }

    public Class<?> getDataType() {
        return dataType.get();
    }

    public ObjectProperty<Object> dataProperty() {
        return data;
    }

    public void setData(Object newData) {
        Object oldData = data.get();
        if (ObjectUtils.compare(oldData, newData)) {
            return;
        }
        data.set(newData);

        if (direction == Port.Direction.INPUT) {
            preprocessData(newData);
        }
    }

    private final ChangeListener<Object> dataListener = this::onDataChanged;

    private void onDataChanged(Object b, Object o, Object n) {
        if (direction == Port.Direction.OUTPUT) {
            publishData();
        }
    }

    private void publishData() {
        for (ConnectionModel connection : connections) {
            connection.forwardData();
        }
    }

    public void preprocessData(Object value) {

        if (!connections.isEmpty()) { // incoming data of one single incoming connection
            System.out.println(block.type() + " received: " + value);

            //Cast all primitive dataType to String if this port dataType is String
            PortModel startPort = connections.iterator().next().getStartPort();
            var incoming = startPort.getData();
            System.out.println("DATA TYPES " + this.getDataType().getSimpleName() + " " + startPort.getDataType().getSimpleName());

            if (this.getDataType() == String.class && TypeCastUtils.contains(startPort.getDataType())) {
                var effective = objectToString(incoming);
                data.set(effective);

            } else if (this.getDataType() == File.class && Path.class.isAssignableFrom(startPort.getDataType())) {
                var effective = pathToFile(incoming);
                data.set(effective);

            } else if (Path.class.isAssignableFrom(this.getDataType()) && startPort.getDataType() == File.class) {
                var effective = fileToPath(incoming);
                data.set(effective);

            } else { // this INPUT port does NOT have data type String

                data.set(incoming);
            }
        }
    }

    private Object objectToString(Object data) {
        return convert(data, Object.class, o -> o + "");
    }

    private Object pathToFile(Object data) {
        System.out.println("PATH TO FILE NOT CALLED");
        return convert(data, Path.class, Path::toFile);
    }

    private Object fileToPath(Object data) {
        return convert(data, File.class, File::toPath);
    }

    private <FROM, TO> Object convert(Object data, Class<FROM> from, Function<FROM, TO> converter) {
        if (data instanceof List) {
            var list = (List<FROM>) data;
            var newList = new ArrayList<TO>();
            for (var item : list) {
                var converted = converter.apply(from.cast(item));
                newList.add(converted);
            }
            return newList;
        } else {
            return converter.apply(from.cast(data));
        }
    }

    public Object getData() {
        return data.get();
    }

    public void addConnection(ConnectionModel connection) {
        connections.add(connection);
        active.set(true);

        if (direction == Port.Direction.INPUT) {
            Object sourceData = connection.getStartPort().getData();
            block.onIncomingConnectionAdded(sourceData);
        }
    }

    public void removeConnection(ConnectionModel connection) {

        connections.remove(connection);
        active.set(!connections.isEmpty());

        if (!isActive() && direction == Port.Direction.INPUT) {
            this.data.set(null);
        }

        if (direction == Port.Direction.INPUT) {
            Object sourceData = connection.getStartPort().getData();
            block.onIncomingConnectionRemoved(sourceData);
        }
    }

    public ObservableSet<ConnectionModel> getConnections() {
        return FXCollections.unmodifiableObservableSet(connections);
    }

    public boolean isConnected() {
        return !connections.isEmpty();
    }

    public List<PortModel> getConnectedPorts() {
        List<PortModel> result = new ArrayList<>();
        for (ConnectionModel connection : connections) {
            if (this.direction == Port.Direction.OUTPUT) {
                result.add(connection.getEndPort());
            } else {
                result.add(connection.getStartPort());
            }
        }
        return result;
    }

    @Override
    public void dispose() {
        data.removeListener(dataListener);
        connections.clear();
        super.dispose();
    }

    @Override
    public void revive() {
        data.addListener(dataListener);
        super.revive();
    }

    public PortRef toDomain() {
        return new PortRef(
                BlockId.from(getBlock().getId()),
                direction,
                valueId
        );
    }
}

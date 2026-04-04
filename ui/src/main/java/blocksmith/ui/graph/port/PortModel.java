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
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import blocksmith.ui.graph.base.BaseModel;
import blocksmith.ui.graph.block.BlockModel;
import blocksmith.ui.graph.connection.ConnectionModel;

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
    private final ObservableSet<ConnectionModel> connections = FXCollections.observableSet();

    private final int index;
    private final boolean multiDockAllowed;
    private final BlockModel block;

    public PortModel(
            String valueId,
            String valueName,
            Direction direction,
            ValueType valueType,
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



    public void addConnection(ConnectionModel connection) {
        connections.add(connection);
        active.set(true);

        if (direction == Port.Direction.INPUT) {
            block.onIncomingConnectionAdded();
        }
    }

    public void removeConnection(ConnectionModel connection) {

        connections.remove(connection);
        active.set(!connections.isEmpty());

        if (direction == Port.Direction.INPUT) {
            block.onIncomingConnectionRemoved();
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
        connections.clear();
        super.dispose();
    }

    public PortRef toDomain() {
        return new PortRef(
                BlockId.from(getBlock().getId()),
                direction,
                valueId
        );
    }
}

package blocksmith.ui.graph.connection;

import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.Connection;
import blocksmith.domain.connection.PortRef;
import blocksmith.domain.value.Port;
import blocksmith.ui.graph.port.PortModel;
import blocksmith.ui.graph.base.BaseModel;

/**
 *
 * @author JoostMeulenkamp
 */
public class ConnectionModel extends BaseModel {

    private final PortModel startPort;
    private final PortModel endPort;

    public ConnectionModel(PortModel startPort, PortModel endPort) {
        this.startPort = startPort;
        this.endPort = endPort;
        initialize();
    }

    @Override
    public void setActive(boolean active) {
        if (active == isActive()) {
            return; // no need to forward data again
        }
        super.setActive(active);

        if (isActive()) {
            forwardData(); // initial data flow

            // Force processing when initially activated with null data
            // This allows blocks to handle null input appropriately e.g. throw an exception or fall back to defaults
            if (startPort.getData() == null) {
                endPort.getBlock().processSafely();
            }
        } else {
            // TODO
        }

    }

    public void forwardData() {
        if (!isActive()) {
            return;
        }
        endPort.setData(startPort.getData());
    }

    public boolean isAutoConnectable() {
        return startPort.autoConnectableProperty().get();
    }

    private void initialize() {
        // add connection to ports here, to ensure connections are re-added on revival
        startPort.addConnection(this);
        endPort.addConnection(this);
    }

    public PortModel getStartPort() {
        return startPort;
    }

    public PortModel getEndPort() {
        return endPort;
    }

    @Override
    public void dispose() {
        startPort.removeConnection(this);
        endPort.removeConnection(this);

        // Force processing when removing a connection with null data
        // This maintains consistency with setActive() behavior
        if (startPort.getData() == null) {
            endPort.getBlock().processSafely();
        }
        super.setActive(false);
        super.dispose();

    }

    public Connection toDomain() {
        var from = PortRef.of(
                BlockId.from(startPort.getBlock().getId()),
                Port.Direction.OUTPUT,
                startPort.valueId()
        );
        var to = PortRef.of(
                BlockId.from(endPort.getBlock().getId()),
                Port.Direction.INPUT,
                endPort.valueId()
        );
        return new Connection(from, to);
    }
}

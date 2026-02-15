package blocksmith.app.connection;

import blocksmith.domain.connection.Connection;
import blocksmith.domain.connection.PortRef;
import blocksmith.domain.graph.Graph;
import blocksmith.domain.value.Port;

/**
 *
 * @author joost
 */
public class AddConnection {

    public Graph execute(Graph graph, PortRef from, PortRef to) {

        if (from.blockId().equals(to.blockId())) {
            throw new IllegalStateException("Cannot connect block to itself");
        }

        var fromBlock = graph.block(from.blockId());
        var toBlock = graph.block(to.blockId());
        if (fromBlock.isEmpty() || toBlock.isEmpty()) {
            throw new IllegalStateException("Referenced block does not exist");
        }

        var fromPort = fromBlock.get().port(from.valueId());
        var toPort = toBlock.get().port(to.valueId());
        if (fromPort.isEmpty() || toPort.isEmpty()) {
            throw new IllegalStateException("Referenced port does not exist");
        }

        if (fromPort.get().direction() != Port.Direction.OUTPUT) {
            throw new IllegalArgumentException("Referenced port is not an OUTPUT: " + fromPort.get().valueId());
        }

        if (toPort.get().direction() != Port.Direction.INPUT) {
            throw new IllegalArgumentException("Referenced port is not an INPUT; " + toPort.get().valueId());

        }

        var connection = new Connection(from, to);
        return graph.withConnection(connection);

    }
}

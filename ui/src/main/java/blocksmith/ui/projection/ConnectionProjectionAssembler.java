package blocksmith.ui.projection;

import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.Connection;
import blocksmith.ui.graph.block.MethodBlockNew;
import blocksmith.ui.graph.connection.ConnectionModel;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author joost
 */
public class ConnectionProjectionAssembler {

    public Map<Connection, ConnectionModel> create(Collection<Connection> connections, Map<BlockId, MethodBlockNew> blockIndex) {
        var result = new HashMap<Connection, ConnectionModel>();
        for (var connection : connections) {

            var fromBlock = blockIndex.get(connection.from().blockId());
            var fromPort = fromBlock.getOutputPorts().stream()
                    .filter(p -> p.valueId().equals(connection.from().valueId()))
                    .findFirst()
                    .get();

            var toBlock = blockIndex.get(connection.to().blockId());
            var toPort = toBlock.getInputPorts().stream()
                    .filter(p -> p.valueId().equals(connection.to().valueId()))
                    .findFirst()
                    .get();

            var model = new ConnectionModel(fromPort, toPort);
            model.setActive(true);
            result.put(connection, model);
        }
        return Map.copyOf(result);
    }

}

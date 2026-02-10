package blocksmith.domain.graph;

import blocksmith.domain.connection.Connection;
import blocksmith.domain.connection.PortRef;
import blocksmith.domain.block.Block;
import blocksmith.domain.block.BlockId;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 *
 * @author joostmeulenkamp
 */
public final class GraphValidator {

    private GraphValidator() {

    }

    public static void validate(Graph graph) {
        var connections = graph.connections();
        verifyConnectionUniqueness(connections);
        verifyInputsConnectedOnlyOnce(connections);
        verifyPortsExist(graph);
    }

    private static void verifyConnectionUniqueness(Collection<Connection> connections) {
        var checked = new HashSet<Connection>();
        for (var connection : connections) {
            if (checked.contains(connection)) {
                throw new IllegalStateException("Graph contains a duplicate connection");
            }
            checked.add(connection);
        }
    }

    private static void verifyInputsConnectedOnlyOnce(Collection<Connection> connections) {
        var checked = new HashSet<PortRef>();
        for (var connection : connections) {
            if (checked.contains(connection.to())) {
                throw new IllegalStateException("INPUT port cannot have multiple incoming connections");
            }
            checked.add(connection.to());
        }
    }

    private static void verifyPortsExist(Graph graph) {
        var blockIndex = new HashMap<BlockId, Block>();
        graph.blocks().forEach(e -> blockIndex.put(e.id(), e));

        for (var connection : graph.connections()) {
            verifyPortExists(connection.from(), blockIndex);
            verifyPortExists(connection.to(), blockIndex);
        }
    }

    private static void verifyPortExists(PortRef ref, Map<BlockId, Block> blockIndex) {
        if (!blockIndex.containsKey(ref.blockId())) {
            throw new IllegalStateException("Connection refers to a non-existent block: " + ref.blockId());
        }

        var block = blockIndex.get(ref.blockId());
        for (var port : block.ports()) {
            var portExists = ref.valueId() == port.valueId();
            if (portExists) {
                return;
            }
        }
        throw new IllegalStateException("Connection refers to a non-existent port: " + ref);
    }
}

package blocksmith.domain.graph;

import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.PortRef;
import blocksmith.domain.value.Port;

/**
 *
 * @author joost
 */
public final class ParamStatusResolver {

    private ParamStatusResolver() {

    }

    public static boolean isActive(Graph graph, BlockId id, String valueId) {
        var block = graph.block(id).orElseThrow(() -> new IllegalStateException("Block does NOT exist: " + id));
        var param = block.param(valueId).orElseThrow(() -> new IllegalStateException("Param does NOT exist: " + id + "." + valueId));

        var coupledPort = block.port(Port.Direction.INPUT, valueId);
        if (coupledPort.isEmpty()) {
            return true;
        }
        var port = PortRef.input(id, valueId);
        return graph.connectionsOf(port).isEmpty();
    }

}

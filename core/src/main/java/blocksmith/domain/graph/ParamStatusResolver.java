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

    public static boolean isActive(Graph graph, BlockId blockId, String valueId) {
        var block = graph.block(blockId).orElseThrow(() -> new IllegalStateException("Block does NOT exist: " + blockId));
        var param = block.param(valueId).orElseThrow(() -> new IllegalStateException("Param does NOT exist: " + blockId + "." + valueId));

        var coupledPort = block.port(Port.Direction.INPUT, valueId);
        if (coupledPort.isEmpty()) {
            return true;
        }
        var port = PortRef.input(blockId, valueId);
        return graph.connectionsOf(port).isEmpty();
    }

}

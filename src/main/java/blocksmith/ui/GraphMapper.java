package blocksmith.ui;

import blocksmith.domain.block.Block;
import blocksmith.domain.graph.Connection;
import blocksmith.domain.graph.Graph;
import blocksmith.domain.block.Port;
import blocksmith.domain.graph.PortRef;
import btscore.graph.block.BlockModel;
import btscore.workspace.WorkspaceModel;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author joostmeulenkamp
 */
public final class GraphMapper {

    private GraphMapper() {
        
    }
    
    public static Graph toDomain(WorkspaceModel workspace) {
        var blocks = blocksToDomain(workspace);
        var connections = connectionsToDomain(workspace);

        return new Graph(blocks, connections);
    }

    private static List<Block> blocksToDomain(WorkspaceModel workspace) {
        var result = new ArrayList<Block>();

        for (var block : workspace.getBlockModels()) {

            var ports = portsToDomain(block);
            var domain = new Block(
                    block.getIdAsUuid(),
                    block.getClass().getSimpleName(),
                    ports
            );

            result.add(domain);
        }
        return result;
    }

    private static List<Port> portsToDomain(BlockModel block) {
        var result = new ArrayList<Port>();

        for (var input : block.getInputPorts()) {
            var domain = new Port(Port.Direction.INPUT, input.getIndex(), input.getDataType());
            result.add(domain);
        }

        for (var output : block.getOutputPorts()) {
            var domain = new Port(Port.Direction.OUTPUT, output.getIndex(), output.getDataType());
            result.add(domain);
        }

        return result;
    }

    private static List<Connection> connectionsToDomain(WorkspaceModel workspace) {
        var result = new ArrayList<Connection>();

        for (var connection : workspace.getConnectionModels()) {
            
            var from = connection.getStartPort();
            var to = connection.getEndPort();
            
            var fromRef = new PortRef(
                    from.getBlock().getIdAsUuid(),
                    Port.Direction.OUTPUT,
                    from.getIndex()
            );
            var toRef = new PortRef(
                    to.getBlock().getIdAsUuid(),
                    Port.Direction.INPUT,
                    to.getIndex()
            );

            var domain = new Connection(fromRef, toRef);
            result.add(domain);
        }

        return result;
    }
}

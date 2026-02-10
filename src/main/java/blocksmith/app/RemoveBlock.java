package blocksmith.app;

import blocksmith.domain.block.BlockId;
import blocksmith.domain.graph.Graph;

/**
 *
 * @author joost
 */
public class RemoveBlock {

    public Graph execute(Graph graph, BlockId id) {
        var blocks = graph.blocks().stream().filter(b -> b.id() != id).toList();
        return new Graph(
                graph.metadata(),
                blocks,
                graph.connections(),
                graph.groups()
        );
    }

}

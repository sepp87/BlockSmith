package blocksmith.domain.graph;

import blocksmith.domain.block.BlockId;
import java.util.Collection;
import java.util.HashSet;

/**
 *
 * @author joost
 */
public class GraphUtils {

    public static Collection<BlockId> sinksOf(Graph graph) {
        var result = new HashSet<BlockId>();
        for (var block : graph.blocks()) {
            if (graph.hasOutgoingConnections(block.id())) {
                continue;
            }
            result.add(block.id());
        }
        return result;
    }

    public static Collection<BlockId> originsOf(Graph graph) {
        var result = new HashSet<BlockId>();
        for (var block : graph.blocks()) {
            if (graph.hasIncomingConnections(block.id())) {
                continue;
            }
            result.add(block.id());
        }
        return result;
    }
}

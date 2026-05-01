package blocksmith.exec;

import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.PortRef;
import blocksmith.domain.graph.Graph;
import blocksmith.domain.graph.GraphUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

/**
 *
 * @author joost
 */
public class DependencyGraph {

    private final static Logger LOGGER = Logger.getLogger(DependencyGraph.class.getName());

    public DependencyGraph() {
    }

    public void resolveAll(Graph graph) {

        var index = new HashMap<BlockId, Collection<BlockId>>();

        var sinks = GraphUtils.sinksOf(graph);
        for (var sink : sinks) {
            var visited = new HashSet<BlockId>();
            indexDependenciesOf(graph, sink, index, visited);
        }

        var origins = GraphUtils.originsOf(graph);

    }

    private static void indexDependenciesOf(Graph current, BlockId target, HashMap<BlockId, Collection<BlockId>> index, Set<BlockId> visited) {

        if (index.containsKey(target)) {
            // sink met a cross section, where another sink already caused dependencies to be indexed
            return;
        }

        if (!visited.add(target)) {
            // cycle detected within dependencies of sink, TODO save cyclical dependencies seperate
            return;
        }

        var dependencies = collectDependencies(current, target);
        if (dependencies.isEmpty()) {
            return;
        }
        index.put(target, dependencies);

        for (var connected : dependencies) {
            indexDependenciesOf(current, connected, index, visited);
        }

    }

    private static List<BlockId> collectDependencies(Graph current, BlockId target) {
        var block = current.block(target).orElseThrow();
        var inputs = block.inputRefs();

        var dependencies = new ArrayList<BlockId>();
        for (var input : inputs) {
            var dependency = upstreamDependencyOf(current, input);
            dependency.ifPresent(d -> dependencies.add(d));
        }
        return List.copyOf(dependencies);
    }

    private static Optional<BlockId> upstreamDependencyOf(Graph current, PortRef input) {

        // retrieve upstream - case: unconnected
        var connection = current.incomingConnection(input);
        if (connection.isEmpty()) {
            return Optional.empty();
        }

        // retrieve upstream - case: connected
        var connectedOutput = connection.get().from();
        var connectedBlock = connectedOutput.blockId();
        return Optional.of(connectedBlock);
    }

}

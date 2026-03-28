package blocksmith.exec;

import blocksmith.app.block.BlockDefLibrary;
import blocksmith.app.block.BlockFuncLibrary;
import blocksmith.domain.block.Block;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.PortRef;
import blocksmith.domain.graph.Graph;
import blocksmith.domain.value.Port;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

/**
 *
 * @author joost
 */
public class DependencyGraph {

    private final static Logger LOGGER = Logger.getLogger(DependencyGraph.class.getName());

    public DependencyGraph() {
    }



    // TODO remove state from dependency graph
    public void resolveAll(Graph graph, ExecutionState state) {

        var index = new HashMap<BlockId, Collection<BlockId>>();

        var sinks = resolveSinksOf(graph);
        for (var sink : sinks) {
            var visited = new HashSet<BlockId> ();
            indexDependenciesOf(graph, state, sink, index, visited);
        }
        
        
        var origins = resolveOriginsOf(graph);
        
        
    }

    private static void indexDependenciesOf(Graph current, ExecutionState state, BlockId target, HashMap<BlockId, Collection<BlockId>> index, Set<BlockId> visited) {

        if (index.containsKey(target)) {
            // sink met a cross section, where another sink already caused dependencies to be indexed
            return;
        }
        
        if(!visited.add(target)) {
            // cycle detected within dependencies of sink, TODO save cyclical dependencies seperate
            return;
        }

        var dependencies = collectDependencies(current, state, target);
        if (dependencies.isEmpty()) {
            return;
        }
        index.put(target, dependencies);

        for (var connected : dependencies) {
            indexDependenciesOf(current, state, connected, index, visited);
        }

    }

    private static List<BlockId> collectDependencies(Graph current, ExecutionState state, BlockId target) {
        var block = current.block(target).orElseThrow();
        var inputs = block.inputRefs();

        var dependencies = new ArrayList<BlockId>();
        for (var input : inputs) {
            var dependency = identifyDependencyOf(current, state, input);
            dependency.ifPresent(d -> dependencies.add(d));
        }
        return List.copyOf(dependencies);
    }

    private static Optional<BlockId> identifyDependencyOf(Graph current, ExecutionState state, PortRef input) {

        if (state.hasValueOf(input)) {
            return Optional.empty();
        }

        // retrieve upstream - case: unconnected
        var connection = current.incomingConnection(input);
        if (connection.isEmpty()) {
            return Optional.empty();
        }

        // retrieve upstream - case: connected and provides value
        var connectedOutput = connection.get().from();
        var connectedBlock = connectedOutput.blockId();
        if (state.hasValueOf(connectedOutput)) { // status FINISHED, if value is present
            LOGGER.info("Status of upstream block assumed to be FINISHED, found: " + state.statusOf(connectedBlock));
            return Optional.empty();
        }

        // retrieve upstream - case: connected, but upstream execution failed
        if (state.statusOf(connectedBlock) == BlockStatus.FAILED) {
            return Optional.empty(); // status FAILED, then input value resolves to null
        }

        LOGGER.info("Status of upstream block assumed to be IDLE, found: " + state.statusOf(connectedOutput.blockId()));

        // status RUNNING and IDLE left TBD
        // status IDLE, then this port depends on upstream block to be executed first
        return Optional.of(connectedBlock);
    }

    private Collection<BlockId> resolveOriginsOf(Graph current) {
        var result = new HashSet<BlockId>();
        for (var block : current.blocks()) {
            if (current.hasIncomingConnections(block.id())) {
                continue;
            }
            result.add(block.id());
        }
        return result;
    }

    private Collection<BlockId> resolveSinksOf(Graph current) {
        var result = new HashSet<BlockId>();
        for (var block : current.blocks()) {
            if (current.hasOutgoingConnections(block.id())) {
                continue;
            }
            result.add(block.id());
        }
        return result;
    }
}

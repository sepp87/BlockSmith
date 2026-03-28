package blocksmith.exec;

import blocksmith.domain.block.Block;
import blocksmith.domain.connection.PortRef;
import blocksmith.domain.graph.Graph;
import blocksmith.domain.graph.GraphDiff;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author joost
 */
class ExecutionInvalidator {

    public void invalidate(ExecutionState state, Graph previous, Graph current, GraphDiff changes) {

        var visited = new HashSet<PortRef> ();
        
        changes.addedConnections()
                .forEach(c -> invalidateDownstream(state, current, c.to(), visited));

        var removedBlocks = changes.removedBlocks().stream().map(Block::id).toList();

        changes.removedConnections()
                .stream()
                .filter(c -> !removedBlocks.contains(c.to().blockId()))
                .forEach(c -> {
                    var unconnected = c.to();
                    clearExceptionsIf(state, current, unconnected);
                    invalidateDownstream(state, current, unconnected, visited);
                });

        changes.removedBlocks()
                .forEach(block -> removeBlockFromState(state, block));

        changes.updatedBlocks()
                .forEach(block -> {
                    var updatedParams = paramsChangedSince(previous, block);
                    if (!updatedParams.isEmpty()) {
                        invalidateDownstream(state, current, updatedParams.getFirst(), visited);
                    }
                    updatedParams.stream().skip(1).forEach(p -> state.removeValueOf(p));
                });
    }

    private void invalidateDownstream(ExecutionState state, Graph current, PortRef ref, Set<PortRef> visited) {

        state.removeStatusOf(ref.blockId());
        state.removeValueOf(ref);
        
        if(!visited.add(ref)) {
            return;
        }
        
//        var removed = state.removeValueOf(ref); // if connection was added and no connection before then there is no remove done
//
//        if (!removed) { // already invalidated
//            return;
//        }

        var block = current.block(ref.blockId()).orElseThrow();
        var outputs = block.outputPorts().stream().map(output -> PortRef.output(ref.blockId(), output.valueId())).toList();

        outputs.forEach(p -> state.removeValueOf(p));

        for (var output : outputs) {
            var connected = current.connectionsOf(output).stream().map(c -> c.to()).toList();
            connected.forEach(input -> invalidateDownstream(state, current, input, visited));
        }

    }

    private void clearExceptionsIf(ExecutionState state, Graph current, PortRef ref) {
        var block = current.block(ref.blockId()).orElseThrow();
        if (!current.hasIncomingConnections(block.id())) {
            state.clearExceptionsOf(block.id());
        }
    }

    private void removeBlockFromState(ExecutionState state, Block block) {
        state.clearExceptionsOf(block.id());
        state.removeStatusOf(block.id());

        var ports = block.ports().stream().map(p -> PortRef.of(block.id(), p.direction(), p.valueId())).toList();
        for (var port : ports) {
            state.removeValueOf(port);
        }
    }

    private List<PortRef> paramsChangedSince(Graph previous, Block current) {
        var id = current.id();
        var previousParams = previous.block(id).orElseThrow().params();
        return current.params()
                .stream()
                .filter(param -> !previousParams.contains(param))
                .map(param -> PortRef.input(id, param.valueId()))
                .toList();
    }
       
}

package blocksmith.exec.engine;

import blocksmith.domain.block.Block;
import blocksmith.domain.block.BlockId;
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
public class ExecutionInvalidator {

    public boolean invalidate(ExecutionState state, Graph previous, Graph current, GraphDiff changes) {

        boolean changed = false;
        var visited = new HashSet<PortRef>();


        for (var c : changes.addedConnections()) {
            changed |= invalidateDownstream(state, current, c.to(), visited);
        }

        var removedBlockIds = changes.removedBlocks().stream().map(Block::id).toList();
        for (var c : changes.removedConnections()) {
            if (!removedBlockIds.contains(c.to().blockId())) {
                var unconnected = c.to();
                clearExceptionsIf(state, current, unconnected);
                changed |= invalidateDownstream(state, current, unconnected, visited);
            }
        }

        for (var block : changes.removedBlocks()) {
            changed |= removeBlockFromState(state, block);
        }

        for (var block : changes.updatedBlocks()) {
            var updatedParams = paramsChangedSince(previous, block);
            if (!updatedParams.isEmpty()) {
                changed |= invalidateDownstream(state, current, updatedParams.getFirst(), visited);
                updatedParams.stream().skip(1).forEach(p -> state.removeValueOf(p));
            }
        }

        return changed;
    }

    public void invalidateDownstreamExcluding(ExecutionState state, Graph current, BlockId source) {
        var visited = new HashSet<PortRef>();

        var block = current.block(source).orElseThrow();
        var outputs = block.outputPorts().stream().map(output -> PortRef.output(source, output.valueId())).toList();

        for (var output : outputs) {
            var connected = current.connectionsOf(output).stream().map(c -> c.to()).toList();
            connected.forEach(input -> invalidateDownstream(state, current, input, visited));
        }
    }

    private boolean invalidateDownstream(ExecutionState state, Graph current, PortRef ref, Set<PortRef> visited) {

        boolean changed = state.removeStatusOf(ref.blockId());
        changed |= state.removeValueOf(ref);

        if (!visited.add(ref)) {
            return changed;
        }

        var block = current.block(ref.blockId()).orElseThrow();
        var outputs = block.outputPorts().stream().map(output -> PortRef.output(ref.blockId(), output.valueId())).toList();

        for (var p : outputs) {
            changed |= state.removeValueOf(p);
        }

        for (var output : outputs) {
            for (var input : current.connectionsOf(output).stream().map(c -> c.to()).toList()) {
                changed |= invalidateDownstream(state, current, input, visited);
            }
        }

        return changed;
    }

    private void clearExceptionsIf(ExecutionState state, Graph current, PortRef ref) {
        var block = current.block(ref.blockId()).orElseThrow();
        if (!current.hasIncomingConnections(block.id())) {
            state.clearExceptionsOf(block.id());
        }
    }

    private boolean removeBlockFromState(ExecutionState state, Block block) {
        state.clearExceptionsOf(block.id());
        var changed = state.removeStatusOf(block.id());

        var ports = block.ports().stream().map(p -> PortRef.of(block.id(), p.direction(), p.valueId())).toList();
        for (var port : ports) {
            changed |= state.removeValueOf(port);
        }
        return changed;
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

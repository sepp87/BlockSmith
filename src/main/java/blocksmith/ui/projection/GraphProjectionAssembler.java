package blocksmith.ui.projection;

import blocksmith.domain.block.Block;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.PortRef;
import blocksmith.domain.graph.Graph;
import blocksmith.domain.graph.GraphDiff;
import blocksmith.domain.graph.ValueTypeResolver;
import static blocksmith.domain.graph.ValueTypeResolver.varTypeWithin;
import blocksmith.domain.value.Param;
import blocksmith.domain.value.Port;
import blocksmith.domain.value.ValueType;
import blocksmith.ui.BlockModelFactory;
import blocksmith.ui.graph.block.MethodBlockNew;
import blocksmith.ui.projection.GraphProjection.GraphProjectionState;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author joost
 */
public class GraphProjectionAssembler {

    private final BlockModelFactory blockFactory;
    private final BlockProjectionAssembler blockAssembler;
    private final ConnectionProjectionAssembler connectionAssembler;
    private final GroupProjectionAssembler groupAssembler;

    public GraphProjectionAssembler(BlockModelFactory blockFactory) {
        this.blockFactory = blockFactory;
        this.blockAssembler = new BlockProjectionAssembler(blockFactory);
        this.connectionAssembler = new ConnectionProjectionAssembler();
        this.groupAssembler = new GroupProjectionAssembler();
    }

    public void initializeState(GraphProjectionState state, Graph graph) {

        var blocks = blockAssembler.create(graph.blocks(), graph);
        var connections = connectionAssembler.create(graph.connections(), blocks);
        var groups = groupAssembler.create(graph.groups(), blocks);

        state.blocks().putAll(blocks);
        state.connections().putAll(connections);
        state.groups().putAll(groups);

    }

    public void applyUpdate(GraphProjectionState state, GraphDiff diff, Graph graph) {
        removeAll(state, diff);
        createAll(state, diff, graph);
        updateAll(state, diff, graph);
    }

    private void removeAll(GraphProjectionState state, GraphDiff diff) {
        // remove groups
        for (var group : diff.removedGroups()) {
            var projection = state.groups().remove(group.id());
            projection.dispose();
        }

        // remove connections
        for (var connection : diff.removedConnections()) {
            var projection = state.connections().remove(connection);
            projection.dispose();
        }

        // remove blocks
        for (var block : diff.removedBlocks()) {
            var projection = state.blocks().remove(block.id());
            projection.dispose();
        }
    }

    private void createAll(GraphProjectionState state, GraphDiff diff, Graph graph) {
        // add blocks
        var newBlocks = blockAssembler.create(diff.addedBlocks(), graph);
        state.blocks().putAll(newBlocks);

        // add connections
        var newConnections = connectionAssembler.create(diff.addedConnections(), state.blocks());
        state.connections().putAll(newConnections);

        // add groups
        var newGroups = groupAssembler.create(diff.addedGroups(), state.blocks());
        state.groups().putAll(newGroups);

    }

    private void updateAll(GraphProjectionState state, GraphDiff diff, Graph graph) {

        // update blocks' input control isEditable
        var paramsWithStatusAffected = paramsWithStatusAffected(diff);
        for (var ref : paramsWithStatusAffected) {
            var projection = state.blocks().get(ref.blockId());
            blockAssembler.updateInputControl(projection, ref, graph);
        }

        // update blocks' port value types
        var portsWithValueTypeAffected = portsWithValueTypeAffected(diff, graph);
        for (var ref : portsWithValueTypeAffected) {
            var projection = state.blocks().get(ref.blockId());
            blockAssembler.updatePort(projection, ref, graph);
        }

        // update blocks' layout (label, position, size) or input control value
        for (var block : diff.updatedBlocks()) { // 
            var projection = state.blocks().get(block.id());
            projection.updateFrom(block);
        }

        // update groups
        for (var group : diff.updatedGroups()) {
            var projection = state.groups().get(group.id());
            projection.updateFrom(group, state.blocks());
        }
    }

    private Collection<PortRef> paramsWithStatusAffected(GraphDiff diff) {
        return connectedPortsDownstream(diff);
    }

    private Collection<PortRef> portsWithValueTypeAffected(GraphDiff diff, Graph graph) {
        var candidates = connectedPortsDownstream(diff);
        return portsWithValueTypeAffectedDownstream(graph, candidates, new HashSet<>());
    }

    private Collection<PortRef> connectedPortsDownstream(GraphDiff diff) {
        var result = new HashSet<PortRef>();
        
        diff.addedConnections().forEach(c -> result.add(c.to()));

        var removedBlocks = diff.removedBlocks().stream().map(Block::id).toList();
        diff.removedConnections().stream()
                .filter(c -> !removedBlocks.contains(c.to().blockId()) || !removedBlocks.contains(c.from().blockId()))
                .forEach(c -> result.add(c.to()));
        
        return Set.copyOf(result);
    }

    private Collection<PortRef> portsWithValueTypeAffectedDownstream(Graph graph, Collection<PortRef> candidates, Collection<PortRef> visited) {
        visited.addAll(candidates);

        var result = new HashSet<PortRef>();
        var downstreamCandidates = new HashSet<PortRef>();

        for (var ref : candidates) {
            var block = graph.block(ref.blockId()).orElseThrow();
            var input = block.port(ref.direction(), ref.valueId()).orElseThrow();

            var varType = ValueTypeResolver.varTypeWithin(input.valueType());
            if (varType.isPresent()) {
                result.add(ref);
            } else {
                continue;
            }

            var outputs = ValueTypeResolver.boundOutputsOf(block, varType.get());
            outputs.forEach(o -> {
                var boundOutput = PortRef.output(block.id(), o.valueId());
                result.add(boundOutput);
                graph.connectionsOf(boundOutput).forEach(c -> downstreamCandidates.add(c.to()));
            });
        }

        if (!downstreamCandidates.isEmpty()) {
            var affected = portsWithValueTypeAffectedDownstream(graph, downstreamCandidates, visited);
            result.addAll(affected);
        }

        return result;
    }
 

}

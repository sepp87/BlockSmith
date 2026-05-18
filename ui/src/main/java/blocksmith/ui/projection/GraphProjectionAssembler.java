package blocksmith.ui.projection;

import blocksmith.domain.connection.PortRef;
import blocksmith.domain.graph.Graph;
import blocksmith.domain.graph.GraphDiff;
import blocksmith.domain.value.ValueType;
import blocksmith.exec.engine.ExecutionState;
import blocksmith.ui.graph.block.BlockModelFactory;
import blocksmith.ui.projection.GraphProjection.GraphProjectionState;
import java.util.Map;

/**
 *
 * @author joost
 */
public class GraphProjectionAssembler {

    private final BlockProjectionAssembler blockAssembler;
    private final ConnectionProjectionAssembler connectionAssembler;
    private final GroupProjectionAssembler groupAssembler;

    public GraphProjectionAssembler(BlockModelFactory blockFactory, ExecutionState runtime) {
        this.blockAssembler = new BlockProjectionAssembler(blockFactory, runtime);
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

    public void applyTypeEnvUpdate(GraphProjectionState state, Map<PortRef, ValueType> updatedValueTypes) {
        for (var entry : updatedValueTypes.entrySet()) {
            var ref = entry.getKey();
            var valueType = entry.getValue();
            var projection = state.blocks().get(ref.blockId());
            blockAssembler.updatePort(projection, ref, valueType);
        }
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

        // update blocks' layout (label, position, size) or input control value
        for (var block : diff.updatedBlocks()) { // 
            var projection = state.blocks().get(block.id());
            blockAssembler.updateBlock(projection, block, graph);
        }

        // update groups
        for (var group : diff.updatedGroups()) {
            var projection = state.groups().get(group.id());
            projection.updateFrom(group, state.blocks());
        }
    }

}

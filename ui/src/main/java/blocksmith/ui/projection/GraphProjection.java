package blocksmith.ui.projection;

import blocksmith.domain.block.Block;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.Connection;
import blocksmith.domain.graph.Graph;
import blocksmith.domain.graph.GraphDiff;
import blocksmith.domain.group.Group;
import blocksmith.domain.group.GroupId;
import blocksmith.ui.graph.block.MethodBlockNew;
import blocksmith.ui.graph.connection.ConnectionModel;
import blocksmith.ui.graph.group.BlockGroupModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 *
 * @author joost
 */
public class GraphProjection {

    private final Map<BlockId, MethodBlockNew> blocks = new HashMap<>();
    private final Map<Connection, ConnectionModel> connections = new HashMap<>();
    private final Map<GroupId, BlockGroupModel> groups = new HashMap<>();

    private final GraphProjectionAssembler assembler;
    private final List<Consumer<GraphProjectionDiff>> listeners = new ArrayList<>();

    static record GraphProjectionState(
            Map<BlockId, MethodBlockNew> blocks,
            Map<Connection, ConnectionModel> connections,
            Map<GroupId, BlockGroupModel> groups) {

    }

    GraphProjectionState state() {
        return new GraphProjectionState(blocks, connections, groups);
    }

    public GraphProjection(GraphProjectionAssembler assembler, Graph graph) {
        this.assembler = assembler;

        assembler.initializeState(state(), graph);
    }

    public void updateFromGraphState(Graph oldGraph, Graph newGraph) {
        var diff = GraphDiff.compare(oldGraph, newGraph);

        assembler.applyUpdate(state(), diff, newGraph);

        var projectionDiff = new GraphProjectionDiff(
                diff.addedBlocks().stream().map(Block::id).toList(),
                diff.removedBlocks().stream().map(Block::id).toList(),
                diff.updatedBlocks().stream().map(Block::id).toList(),
                diff.addedConnections(),
                diff.removedConnections(),
                diff.addedGroups().stream().map(Group::id).toList(),
                diff.removedGroups().stream().map(Group::id).toList(),
                diff.updatedGroups().stream().map(Group::id).toList()
        );

        projectionChanged(projectionDiff);
    }

    
    
    public MethodBlockNew block(BlockId id) {
        return blocks.get(id);
    }

    public Collection<MethodBlockNew> blocks(Collection<BlockId> ids) {
        return ids.stream().map(blocks::get).toList();
    }

    public Collection<MethodBlockNew> blocks() {
        return blocks.values();
    }

    public ConnectionModel connection(Connection id) {
        return connections.get(id);
    }

    public BlockGroupModel group(GroupId id) {
        return groups.get(id);
    }

    public void addProjectionListener(Consumer<GraphProjectionDiff> listener) {
        listeners.add(listener);
    }

    private void projectionChanged(GraphProjectionDiff diff) {
        listeners.forEach(c -> c.accept(diff));
    }

}

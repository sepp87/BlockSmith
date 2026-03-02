package btscore.workspace;

import blocksmith.domain.block.Block;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.Connection;
import blocksmith.domain.graph.Graph;
import blocksmith.domain.graph.GraphDiff;
import blocksmith.domain.group.Group;
import blocksmith.domain.group.GroupId;
import blocksmith.ui.MethodBlockNew;
import btscore.graph.block.BlockModel;
import btscore.graph.connection.ConnectionModel;
import btscore.graph.group.BlockGroupModel;
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

    private final Map<BlockId, BlockModel> blocks = new HashMap<>();
    private final Map<Connection, ConnectionModel> connections = new HashMap<>();
    private final Map<GroupId, BlockGroupModel> groups = new HashMap<>();

    private final GraphProjectionMapper mapper;
    private final List<Consumer<GraphProjectionDiff>> listeners = new ArrayList<>();

    public GraphProjection(GraphProjectionMapper mapper, Graph graph) {
        this.mapper = mapper;

        blocks.putAll(mapper.blocksToProjection(graph.blocks()));
        connections.putAll(mapper.connectionsToProjection(graph.connections(), blocks));
        groups.putAll(mapper.groupsToProjection(graph.groups(), blocks));
    }

    public void updateFrom(Graph oldGraph, Graph newGraph) {

        var diff = GraphDiff.compare(oldGraph, newGraph);

        // remove groups
        for (var group : diff.removedGroups()) {
            var projection = groups.get(group.id());
            projection.dispose();
        }

        // remove connections
        for (var connection : diff.removedConnections()) {
            var projection = connections.remove(connection);
            projection.dispose();
        }

        // remove blocks
        for (var block : diff.removedBlocks()) {
            var projection = blocks.remove(block.id());
            projection.dispose();
        }

        // add blocks
        var newBlocks = mapper.blocksToProjection(diff.addedBlocks());
        blocks.putAll(newBlocks);

        // add connections
        var newConnections = mapper.connectionsToProjection(diff.addedConnections(), blocks);
        connections.putAll(newConnections);

        // add groups
        var newGroups = mapper.groupsToProjection(diff.addedGroups(), blocks);
        groups.putAll(newGroups);

        // update blocks
        for (var block : diff.updatedBlocks()) {
            var projection = blocks.get(block.id());
            if (projection instanceof MethodBlockNew mbn) {
                mbn.updateFrom(block);
            }
        }

        // update groups
        for (var group : diff.updatedGroups()) {
            var projection = groups.get(group.id());
            projection.updateFrom(group, blocks);
        }

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

    public Collection<BlockModel> blocks(Collection<BlockId> ids) {
        return ids.stream().map(blocks::get).toList();
    }

    public Collection<BlockModel> blocks() {
        return blocks.values();
    }

    public void addProjectionListener(Consumer<GraphProjectionDiff> listener) {
        listeners.add(listener);
    }

    private void projectionChanged(GraphProjectionDiff diff) {
        listeners.forEach(c -> c.accept(diff));
    }

}

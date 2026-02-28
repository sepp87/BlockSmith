package btscore.workspace;

import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.Connection;
import blocksmith.domain.graph.Graph;
import blocksmith.domain.graph.GraphDiff;
import blocksmith.domain.group.GroupId;
import blocksmith.ui.MethodBlockNew;
import btscore.graph.block.BlockModel;
import btscore.graph.connection.ConnectionModel;
import btscore.graph.group.BlockGroupModel;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author joost
 */
public class GraphProjection {

    private final Map<BlockId, BlockModel> blocks = new HashMap<>();
    private final Map<Connection, ConnectionModel> connections = new HashMap<>();
    private final Map<GroupId, BlockGroupModel> groups = new HashMap<>();

    private final GraphProjectionMapper mapper;

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

    }

    public Collection<BlockModel> blocks(Collection<BlockId> ids) {
        return ids.stream().map(blocks::get).toList();
    }

}

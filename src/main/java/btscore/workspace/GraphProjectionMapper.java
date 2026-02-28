package btscore.workspace;

import blocksmith.domain.block.Block;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.Connection;
import blocksmith.domain.group.Group;
import blocksmith.domain.group.GroupId;
import blocksmith.ui.BlockModelFactory;
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
public class GraphProjectionMapper {

    private final BlockModelFactory blockFactory;

    public GraphProjectionMapper(BlockModelFactory blockFactory) {
        this.blockFactory = blockFactory;

    }

    public Map<BlockId, BlockModel> blocksToProjection(Collection<Block> blocks) {
        var result = new HashMap<BlockId, BlockModel>();
        for (var block : blocks) {
            var model = blockFactory.create(block.type(), block.id().toString());
            model.updateFrom(block);
            result.put(block.id(), model);
        }
        return Map.copyOf(result);
    }

    public Map<Connection, ConnectionModel> connectionsToProjection(Collection<Connection> connections, Map<BlockId, BlockModel> blockIndex) {
        var result = new HashMap<Connection, ConnectionModel>();
        for (var connection : connections) {

            var fromBlock = blockIndex.get(connection.from().blockId());
            var fromPort = fromBlock.getOutputPorts().stream()
                    .filter(p -> p.labelProperty().get().equals(connection.from().valueId()))
                    .findFirst()
                    .get();

            var toBlock = blockIndex.get(connection.to().blockId());
            var toPort = toBlock.getInputPorts().stream()
                    .filter(p -> p.labelProperty().get().equals(connection.to().valueId()))
                    .findFirst()
                    .get();

            var model = new ConnectionModel(fromPort, toPort);
            result.put(connection, model);
        }
        return Map.copyOf(result);
    }

    public Map<GroupId, BlockGroupModel> groupsToProjection(Collection<Group> groups, Map<BlockId, BlockModel> blockIndex) {
        var result = new HashMap<GroupId, BlockGroupModel>();
        for (var group : groups) {
            var model = new BlockGroupModel(group.id().toString());
            model.labelProperty().set(group.label());
            group.blocks().forEach(b -> model.addBlock(blockIndex.get(b)));
            result.put(group.id(), model);
        }
        return Map.copyOf(result);
    }
}

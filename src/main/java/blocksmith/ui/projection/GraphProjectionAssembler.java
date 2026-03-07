package blocksmith.ui.projection;

import blocksmith.domain.block.Block;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.Connection;
import blocksmith.domain.graph.Graph;
import blocksmith.domain.graph.GraphDiff;
import blocksmith.domain.group.Group;
import blocksmith.domain.group.GroupId;
import blocksmith.ui.BlockModelFactory;
import blocksmith.ui.graph.block.BlockModel;
import blocksmith.ui.graph.connection.ConnectionModel;
import blocksmith.ui.graph.group.BlockGroupModel;
import blocksmith.ui.projection.GraphProjection.GraphProjectionState;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
        this.connectionAssembler = new ConnectionProjectionAssembler ();
        this.groupAssembler = new GroupProjectionAssembler();
    }
    
    public void create(GraphProjectionState state, Graph graph) {
        
        var blocks = state.blocks();
        var connections = state.connections();
        var groups = state.groups();
        
        blocks.putAll(blockAssembler.create(graph.blocks()));
        connections.putAll(connectionAssembler.create(graph.connections(), blocks));
        groups.putAll(groupAssembler.create(graph.groups(), blocks));
        
        
    }
    
    public Map<BlockId, BlockModel> blocksToProjection(Collection<Block> blocks) {
        var result = new HashMap<BlockId, BlockModel>();
        for (var block : blocks) {
            var model = blockFactory.create(block.type(), block.id().toString());
            model.updateFrom(block);
            model.setActive(true);
            result.put(block.id(), model);
    }
        return Map.copyOf(result);
    }



//        public Map<BlockId, BlockModel> blocksToProjection(Collection<Block> blocks) {
//        var result = new HashMap<BlockId, BlockModel>();
//        for (var block : blocks) {
//            var model = blockFactory.create(block.type(), block.id().toString());
//            model.updateLabel(block.label);
//            model.updatePosition(block.position);
//            model.updateSize(block.size);
//            model.updatePortValueTypes(resolveValueTypes(block, graph)); 
//            model.updateInputControls(resolveParamsState(block, graphs));
//            model.setActive(true);
//            result.put(block.id(), model);
//        }
//        return Map.copyOf(result);
//    }


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
            model.setActive(true);
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




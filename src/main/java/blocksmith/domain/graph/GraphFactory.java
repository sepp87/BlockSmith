package blocksmith.domain.graph;

import blocksmith.domain.block.Block;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.Connection;
import blocksmith.domain.group.Group;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author joost
 */
public class GraphFactory {

    public static Graph createEmpty() {
        return new Graph(GraphId.create(), List.of(), List.of(), List.of());
    }

    /**
     * factory method to ensure all params are activated or deactivated if their
     * corresponding ports are connected. Used primarily for copy/paste and
     * loading graphs.
     *
     * @param id
     * @param blocks
     * @param connections
     * @param groups
     * @return
     */
    public static Graph create(GraphId id, Collection<Block> blocks, Collection<Connection> connections, Collection<Group> groups) {
        var blockIndex = new HashMap<BlockId, Block>(indexBlocks(blocks));

        // reset all params to activate
        for (var block : blocks) {
            var params = block.params();
            var updated = block;
            for (var param : params) {
                if (!param.isActive()) {
                    var valueId = param.valueId();
                    updated = updated.withParamActivated(valueId);
                }
            }
            if (block.equals(updated)) {
                continue;
            }
            blockIndex.put(block.id(), updated);
        }

        // deactivate params if the corresponding port is connect
        for (var connection : connections) {
            var blockId = connection.to().blockId();
            var valueId = connection.to().valueId();
            var block = blockIndex.get(blockId);
            if (block.param(valueId).isPresent()) {
                var updated = block.withParamDeactivated(valueId);
                blockIndex.put(blockId, updated);
            }
        }

        return new Graph(GraphId.create(), blockIndex.values(), connections, groups);
    }

    private static Map<BlockId, Block> indexBlocks(Collection<Block> blocks) {
        // TODO duplicate method also in Graph
        Objects.requireNonNull(blocks);
        return blocks.stream().collect(Collectors.toMap(Block::id, Function.identity()));
    }
}

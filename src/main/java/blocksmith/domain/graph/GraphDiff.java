package blocksmith.domain.graph;

import blocksmith.domain.block.Block;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @param connectionsOnlyInA
 * @param blocks
 * @param connections
 * @param groups
 */
public record GraphDiff(
        Collection<Connection> removedConnections, // removed connections
        Collection<Connection> addedConnections, // added connections
        Collection<Block> removedBlocks, // removed blocks
        Collection<Block> addedBlocks, // added blocks
        Collection<Block> updatedBlocks
        ) {

    
    
    // diff of graphs
    public static GraphDiff compare(Graph current, Graph updated) {

        // connections
        var connectionsA = new ArrayList<>(current.connections());
        var connectionsB = new ArrayList<>(updated.connections());

        var connectionsOnlyInB = new ArrayList<Connection>();

        for (var b : connectionsB) {
            var contained = connectionsA.remove(b);
            if (!contained) {
                connectionsOnlyInB.add(b);
            }
        }
        var connectionsOnlyInA = connectionsA;

        // blocks
        var blocksA = toMap(current.blocks());
        var blocksB = new ArrayList<>(updated.blocks());

        var blocksOnlyInB = new ArrayList<Block>();
        var updatedBlocks = new ArrayList<Block>();

        for (var b : blocksB) {
            var a = blocksA.remove(b.id());
            var contained = a != null;
            if (!contained) {
                blocksOnlyInB.add(b);
                continue;
            }
            // in both maps, check if equals
            if (!a.equals(b)) {
                updatedBlocks.add(b);
            }

        }
        var blocksOnlyInA = blocksA.values();

        return new GraphDiff(connectionsOnlyInA, connectionsOnlyInB, blocksOnlyInA, blocksOnlyInB, updatedBlocks);
    }

    private static Map<BlockId, Block> toMap(Collection<Block> blocks) {
        // TODO check for duplicate ids
        Objects.requireNonNull(blocks);
        var result = new HashMap<BlockId, Block>();
        blocks.forEach(b -> result.put(b.id(), b));
        return result;
    }

}

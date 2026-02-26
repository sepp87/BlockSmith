package blocksmith.domain.graph;

import blocksmith.domain.block.Block;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.Connection;
import blocksmith.domain.group.Group;
import blocksmith.domain.group.GroupId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public record GraphDiff(
        Collection<Connection> removedConnections,
        Collection<Connection> addedConnections,
        Collection<Block> removedBlocks,
        Collection<Block> addedBlocks,
        Collection<Block> updatedBlocks,
        Collection<Group> removedGroups,
        Collection<Group> addedGroups,
        Collection<Group> updatedGroups) {

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
        var blocksA = indexBlocks(current.blocks());
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

        // groups
        var groupsA = indexGroups(current.groups());
        var groupsB = updated.groups();

        var groupsOnlyInB = new ArrayList<Group>();
        var updatedGroups = new ArrayList<Group>();
        for (var b : groupsB) {
            var a = groupsA.remove(b.id());
            var contained = a != null;
            if (!contained) {
                groupsOnlyInB.add(b);
                continue;
            }
            // in both maps, check if equals
            if (a.size() != b.size() || !a.blocks().containsAll(b.blocks())) {
                updatedGroups.add(b);
            }

        }
        var groupsOnlyInA = groupsA.values();

        return new GraphDiff(
                connectionsOnlyInA,
                connectionsOnlyInB,
                blocksOnlyInA,
                blocksOnlyInB, 
                updatedBlocks,
                groupsOnlyInA,
                groupsOnlyInB, 
                updatedGroups
        );
    }

    private static Map<BlockId, Block> indexBlocks(Collection<Block> blocks) {
        var result = new HashMap<BlockId, Block>();
        blocks.forEach(b -> result.put(b.id(), b));
        return result;
    }

    private static Map<GroupId, Group> indexGroups(Collection<Group> groups) {
        var result = new HashMap<GroupId, Group>();
        groups.forEach(g -> result.put(g.id(), g));
        return result;
    }

    private static Map<BlockId, GroupId> indexBlocksToGroups(Collection<Group> groups) {
        var result = new HashMap<BlockId, GroupId>();
        for (var group : groups) {
            for (var block : group.blocks()) {
                result.put(block, group.id());
            }
        }
        return result;
    }

}

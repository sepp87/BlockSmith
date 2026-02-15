package blocksmith.domain.graph;

import blocksmith.domain.connection.Connection;
import blocksmith.domain.block.Block;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.group.Group;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author joost
 */
public final class Graph {

    private final List<Block> blocks;
    private final List<Connection> connections;
    private final List<Group> groups;

    public Graph(Collection<Block> blocks, Collection<Connection> connections, Collection<Group> groups) {
        this.blocks = List.copyOf(Objects.requireNonNull(blocks));
        this.connections = List.copyOf(Objects.requireNonNull(connections));
        this.groups = List.copyOf(Objects.requireNonNull(groups));
    }

    public Collection<Block> blocks() {
        return blocks;
    }

    public Collection<Connection> connections() {
        return connections;
    }

    public Collection<Group> groups() {
        return groups;
    }

    public Optional<Block> block(BlockId id) {
        return blocks.stream().filter(b -> b.id().equals(id)).findFirst();
    }

    public Graph withBlock(Block block) {
        var updated = new ArrayList<Block>(blocks);
        updated.add(block);
        return new Graph(updated, connections, groups);
    }

    public Graph withoutBlock(BlockId id) {
        var candidate = block(id);
        if (candidate.isEmpty()) {
            return this;
        }

        var updatedBlocks = blocks.stream()
                .filter(b -> !b.id().equals(id))
                .toList();

        var updatedConnections = connections.stream()
                .filter(c -> !c.from().blockId().equals(id) && !c.to().blockId().equals(id))
                .toList();

        var updatedGroups = groups.stream()
                .map(g -> g.withoutBlock(id))
                .filter(g -> !g.isEmpty())
                .toList();

        return new Graph( updatedBlocks, updatedConnections, updatedGroups);
    }

    public Graph withoutBlocks(Collection<BlockId> ids) {
        if (ids.isEmpty()) {
            return this;
        }

        var updatedBlocks = blocks.stream()
                .filter(b -> !ids.contains(b))
                .toList();

        var updatedConnections = connections.stream()
                .filter(c -> !ids.contains(c.from().blockId()) && !ids.contains(c.to().blockId()))
                .toList();

        var updatedGroups = groups.stream()
                .map(g -> {
                    for (var id : ids) {
                        g = g.withoutBlock(id);
                    }
                    return g;
                })
                .filter(g -> !g.isEmpty())
                .toList();

        return new Graph(updatedBlocks, updatedConnections, updatedGroups);
    }

    public Graph withConnection(Connection connection) {
        var updatedConnections = new ArrayList<Connection>(connections);
        updatedConnections.add(connection);

        var toBlock = connection.to().blockId();
        var toValue = connection.to().valueId();

        var updatedBlocks = blocks.stream()
                .map(b -> b.id().equals(toBlock) ? b.withParamDeactivated(toValue) : b)
                .toList();

        return new Graph(updatedBlocks, updatedConnections, groups);
    }

    public Graph withoutConnection(Connection connection) {
        var updatedConnections = connections.stream()
                .filter(c -> !c.equals(connection))
                .toList();

        var toBlock = connection.to().blockId();
        var toValue = connection.to().valueId();

        var updatedBlocks = blocks.stream()
                .map(b -> b.id().equals(toBlock) ? b.withParamActivated(toValue) : b)
                .toList();

        return new Graph(updatedBlocks, updatedConnections, groups);

    }

    public Graph withGroup(Group group) {
        var updated = new ArrayList<Group>(groups);
        updated.add(group);
        return new Graph(blocks, connections, updated);
    }

    public Graph withoutGroup(Group group) {
        var updated = new ArrayList<Group>(groups);
        updated.remove(group);
        return new Graph(blocks, connections, updated);
    }

    public List<Connection> connectionsOf(Block block) {
        return connections.stream()
                .filter(c -> c.from().blockId().equals(block.id()) || c.to().blockId().equals(block.id()))
                .toList();
    }
    
    public static Graph createEmpty() {
        return new Graph(List.of(), List.of(), List.of());
    }
}

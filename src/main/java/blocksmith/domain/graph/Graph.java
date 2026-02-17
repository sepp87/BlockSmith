package blocksmith.domain.graph;

import blocksmith.domain.connection.Connection;
import blocksmith.domain.block.Block;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.block.BlockPosition;
import blocksmith.domain.group.Group;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author joost
 */
public final class Graph {

    private final GraphId id;
    private final Map<BlockId, Block> blocks;
    private final List<Connection> connections;
    private final List<Group> groups;

    public Graph(GraphId id, Collection<Block> blocks, Collection<Connection> connections, Collection<Group> groups) {
        this.id = Objects.requireNonNull(id);
        this.blocks = toMap(blocks);
        this.connections = List.copyOf(Objects.requireNonNull(connections));
        this.groups = List.copyOf(Objects.requireNonNull(groups));
    }

    private Map<BlockId, Block> toMap(Collection<Block> blocks) {
        // TODO check for duplicate ids
        Objects.requireNonNull(blocks);
        var result = new HashMap<BlockId, Block>();
        blocks.forEach(b -> result.put(b.id(), b));
        return Map.copyOf(result);
    }

    public GraphId id() {
        return id;
    }

    public Collection<Block> blocks() {
        return blocks.values();
    }

    public Collection<Connection> connections() {
        return connections;
    }

    public Collection<Group> groups() {
        return groups;
    }

    public Optional<Block> block(BlockId id) {
        return blocks.values().stream().filter(b -> b.id().equals(id)).findFirst();
    }

    public Graph withBlock(Block block) {
        var updated = new ArrayList<Block>(blocks.values());
        updated.add(block);
        return withAll(updated, connections, groups);
    }

    public Graph withoutBlock(BlockId id) {
        var candidate = block(id);
        if (candidate.isEmpty()) {
            return this;
        }

        var updatedBlocks = blocks.values().stream()
                .filter(b -> !b.id().equals(id))
                .toList();

        var updatedConnections = connections.stream()
                .filter(c -> !c.from().blockId().equals(id) && !c.to().blockId().equals(id))
                .toList();

        var updatedGroups = groups.stream()
                .map(g -> g.withoutBlock(id))
                .filter(g -> !g.isEmpty())
                .toList();

        return new Graph(this.id, updatedBlocks, updatedConnections, updatedGroups);
    }

    public Graph withoutBlocks(Collection<BlockId> ids) {
        if (ids.isEmpty()) {
            return this;
        }

        var updatedBlocks = blocks.values().stream()
                .filter(b -> !ids.contains(b.id()))
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

        return withAll(updatedBlocks, updatedConnections, updatedGroups);
    }

    public Graph updateParamValue(BlockId id, String valueId, String value) {
        var updatedBlocks = new HashMap<BlockId, Block>(blocks);
        updatedBlocks.computeIfPresent(id, (k, v) -> v.withParamValue(valueId, value));
        return new Graph(this.id, updatedBlocks.values(), connections, groups);
    }

    public Graph renameBlock(BlockId id, String label) {
        var updatedBlocks = new HashMap<BlockId, Block>(blocks);
        updatedBlocks.computeIfPresent(id, (k, v) -> v.withLabel(label));
        return new Graph(this.id, updatedBlocks.values(), connections, groups);
    }

    public Graph moveBlocks(Collection<BlockPosition> positions) {
        if (positions.isEmpty()) {
            return this;
        }
        var updatedBlocks = new HashMap<BlockId, Block>(blocks);
        for (var pos : positions) {
            updatedBlocks.computeIfPresent(pos.id(), (k, v) -> v.withPosition(pos.x(), pos.y()));
        }
        return withAll(updatedBlocks.values(), connections, groups);
    }

    public Graph resizeBlock(BlockId id, double width, double height) {
        var updatedBlocks = new HashMap<BlockId, Block>(blocks);
        updatedBlocks.computeIfPresent(id, (k, v) -> v.withSize(width, height));
        return new Graph(this.id, updatedBlocks.values(), connections, groups);
    }

    public Graph withConnection(Connection connection) {
        var updatedConnections = new ArrayList<Connection>(connections);
        updatedConnections.add(connection);

        var toBlock = connection.to().blockId();
        var toValue = connection.to().valueId();

        var updatedBlocks = blocks.values().stream()
                .map(b -> b.id().equals(toBlock) ? b.withParamDeactivated(toValue) : b)
                .toList();

        return withAll(updatedBlocks, updatedConnections, groups);
    }

    public Graph withoutConnection(Connection connection) {
        var updatedConnections = connections.stream()
                .filter(c -> !c.equals(connection))
                .toList();

        var toBlock = connection.to().blockId();
        var toValue = connection.to().valueId();

        var updatedBlocks = blocks.values().stream()
                .map(b -> b.id().equals(toBlock) ? b.withParamActivated(toValue) : b)
                .toList();

        return withAll(updatedBlocks, updatedConnections, groups);

    }

    public Graph withGroup(Group group) {
        var updated = new ArrayList<Group>(groups);
        updated.add(group);
        return withAll(blocks.values(), connections, updated);
    }

    public Graph withoutGroup(Group group) {
        var updated = new ArrayList<Group>(groups);
        updated.remove(group);
        return withAll(blocks.values(), connections, updated);
    }

    public List<Connection> connectionsOf(Block block) {
        return connections.stream()
                .filter(c -> c.from().blockId().equals(block.id()) || c.to().blockId().equals(block.id()))
                .toList();
    }

    public static Graph withAll(Collection<Block> blocks, Collection<Connection> connections, Collection<Group> groups) {
        return withAll(blocks, connections, groups);
    }

    public static Graph createEmpty() {
        return new Graph(GraphId.create(), List.of(), List.of(), List.of());
    }

}

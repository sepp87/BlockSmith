package blocksmith.domain.graph;

import blocksmith.domain.connection.Connection;
import blocksmith.domain.block.Block;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.block.BlockPosition;
import blocksmith.domain.connection.PortRef;
import blocksmith.domain.group.Group;
import blocksmith.domain.group.GroupId;
import blocksmith.domain.value.Port;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author joost
 */
public final class Graph {

    private final GraphId id;
    private final Map<BlockId, Block> blocks;
    private final List<Connection> connections;
    private final Map<GroupId, Group> groups;
    private final Map<BlockId, GroupId> blocksToGroups;

    Graph(GraphId id, Collection<Block> blocks, Collection<Connection> connections, Collection<Group> groups) {
        this.id = Objects.requireNonNull(id);
        this.blocks = indexBlocks(blocks);
        this.connections = List.copyOf(Objects.requireNonNull(connections));
        this.groups = indexGroups(groups);
        this.blocksToGroups = indexBlocksToGroups(groups);
    }

    private static Map<BlockId, Block> indexBlocks(Collection<Block> blocks) {
        // TODO check for duplicate ids
        Objects.requireNonNull(blocks);
        return blocks.stream().collect(Collectors.toMap(Block::id, Function.identity()));
    }

    private static Map<GroupId, Group> indexGroups(Collection<Group> groups) {
        // TODO check for duplicate ids
        Objects.requireNonNull(groups);
        return groups.stream().collect(Collectors.toMap(Group::id, Function.identity()));
    }

    private static Map<BlockId, GroupId> indexBlocksToGroups(Collection<Group> groups) {
        // TODO check for duplicate ids
        Objects.requireNonNull(groups);
        var result = new HashMap<BlockId, GroupId>();
        for (var group : groups) {
            for (var block : group.blocks()) {
                result.put(block, group.id());
            }
        }
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
        return groups.values();
    }

    public Optional<Block> block(BlockId id) {
        return Optional.ofNullable(blocks.get(id));
    }

    public Graph withBlock(Block block) {
        var updated = new ArrayList<Block>(blocks.values());
        updated.add(block);
        return replace(updated, connections, groups.values());
    }

    public Graph withoutBlock(BlockId id) {
        var candidate = block(id);
        if (candidate.isEmpty()) {
            return this;
        }
        return withoutBlocks(List.of(id));
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

        var updatedGroups = new ArrayList<Group>(groups.values());
        for (var id : ids) {
            groupOf(id).ifPresent(groupId -> {
                var group = groups.get(groupId);
                updatedGroups.remove(group);
                if (group.size() > Group.MINIMUM_SIZE) {
                    updatedGroups.add(group.withoutBlock(id));
                }
            });
        }

        return replace(updatedBlocks, updatedConnections, updatedGroups);
    }

    public Graph updateParamValue(BlockId id, String valueId, String value) {
        var existing = blocks.get(id).param(valueId).get().value();
        if (Objects.equals(existing, value)) {
            return this;
        }
        var updatedBlocks = new HashMap<BlockId, Block>(blocks);
        updatedBlocks.computeIfPresent(id, (k, v) -> v.withParamValue(valueId, value));
        return new Graph(this.id, updatedBlocks.values(), connections, groups.values());
    }

    public Graph renameBlock(BlockId id, String label) {
        var existing = blocks.get(id).layout().label();
        if (Objects.equals(existing, label)) {
            return this;
        }
        var updatedBlocks = new HashMap<BlockId, Block>(blocks);
        updatedBlocks.computeIfPresent(id, (k, v) -> v.withLabel(label));
        return new Graph(this.id, updatedBlocks.values(), connections, groups.values());
    }

    public Graph moveBlocks(Collection<BlockPosition> positions) {
        if (positions.isEmpty()) {
            return this;
        }
        var updatedBlocks = new HashMap<BlockId, Block>(blocks);
        for (var pos : positions) {
            updatedBlocks.computeIfPresent(pos.id(), (k, v) -> v.withPosition(pos.x(), pos.y()));
        }
        return replace(updatedBlocks.values(), connections, groups.values());
    }

    public Graph resizeBlock(BlockId id, double width, double height) {
        var updatedBlocks = new HashMap<BlockId, Block>(blocks);
        updatedBlocks.computeIfPresent(id, (k, v) -> v.withSize(width, height));
        return new Graph(this.id, updatedBlocks.values(), connections, groups.values());
    }

    public Graph withConnection(Connection connection) {
        if (connections.contains(connection)) {
            return this;
        }

        var updatedConnections = new ArrayList<Connection>();

        // replace connection, if target port is already connected
        for (var existing : connections) {
            if (existing.to().equals(connection.to())) {
                continue;
            }
            updatedConnections.add(existing);
        }
        updatedConnections.add(connection);

        var toBlock = connection.to().blockId();
        var toValue = connection.to().valueId();

        var updatedBlocks = blocks.values().stream()
                .map(b -> b.id().equals(toBlock) ? b.withParamDeactivated(toValue) : b)
                .toList();

        return replace(updatedBlocks, updatedConnections, groups.values());
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

        return replace(updatedBlocks, updatedConnections, groups.values());
    }

    public Optional<Group> group(GroupId id) {
        return Optional.ofNullable(groups.get(id));
    }

    public Graph withGroup(Group group) {
        var updated = new ArrayList<Group>(groups.values());
        updated.add(group);
        return replace(blocks.values(), connections, updated);
    }

    public Graph withoutGroup(GroupId id) {
        var candidate = group(id);
        if (candidate.isEmpty()) {
            return this;
        }
        var updated = new HashMap<GroupId, Group>(groups);
        updated.remove(id);

        return replace(blocks.values(), connections, updated.values());
    }

    public List<Connection> connectionsOf(BlockId block) {
        return connections.stream()
                .filter(c -> c.from().blockId().equals(block) || c.to().blockId().equals(block))
                .toList();
    }

    public List<Connection> connectionsOf(PortRef ref) {
        if (ref.direction() == Port.Direction.OUTPUT) {
            return connections.stream()
                    .filter(c -> c.from().equals(ref))
                    .toList();
        }

        return connections.stream()
                .filter(c -> c.to().equals(ref))
                .toList();
    }

    public Optional<GroupId> groupOf(BlockId block) {
        return Optional.ofNullable(blocksToGroups.get(block));
    }

    private Graph replace(Collection<Block> blocks, Collection<Connection> connections, Collection<Group> groups) {
        return new Graph(id, blocks, connections, groups);
    }

}

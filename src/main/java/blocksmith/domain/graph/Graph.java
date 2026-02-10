package blocksmith.domain.graph;

import blocksmith.domain.connection.Connection;
import blocksmith.domain.block.Block;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.group.Group;
import com.google.gson.internal.Streams;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 *
 * @author joost
 */
public final class Graph {

    private final DocumentMetadata metadata;
    private final List<Block> blocks;
    private final List<Connection> connections;
    private final List<Group> groups;

    public Graph(DocumentMetadata metadata, Collection<Block> blocks, Collection<Connection> connections, Collection<Group> groups) {
        this.metadata = metadata;
        this.blocks = List.copyOf(Objects.requireNonNull(blocks));
        this.connections = List.copyOf(Objects.requireNonNull(connections));
        this.groups = List.copyOf(Objects.requireNonNull(groups));
    }

    public DocumentMetadata metadata() {
        return metadata;
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
        return new Graph(metadata, updated, connections, groups);
    }

    public Graph withConnection(Connection connection) {
        var updated = new ArrayList<Connection>(connections);
        updated.add(connection);
        return new Graph(metadata, blocks, updated, groups);
    }

    public Graph withGroup(Group group) {
        var updated = new ArrayList<Group>(groups);
        updated.add(group);
        return new Graph(metadata, blocks, connections, updated);
    }            

}

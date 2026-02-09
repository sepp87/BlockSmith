package blocksmith.domain.graph;

import blocksmith.domain.connection.Connection;
import blocksmith.domain.block.Block;
import blocksmith.domain.group.Group;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

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
}

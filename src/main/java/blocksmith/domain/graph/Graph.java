package blocksmith.domain.graph;

import blocksmith.domain.connection.Connection;
import blocksmith.domain.block.Block;
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

    public Graph(DocumentMetadata metadata, List<Block> blocks, List<Connection> connections) {
        this.metadata = metadata;
        this.blocks = List.copyOf(Objects.requireNonNull(blocks));
        this.connections = List.copyOf(Objects.requireNonNull(connections));
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

}

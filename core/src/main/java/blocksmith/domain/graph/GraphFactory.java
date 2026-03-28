package blocksmith.domain.graph;

import blocksmith.domain.block.Block;
import blocksmith.domain.connection.Connection;
import blocksmith.domain.group.Group;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author joost
 */
public class GraphFactory {

    public static Graph createEmpty() {
        return new Graph(GraphId.create(), List.of(), List.of(), List.of());
    }


    public static Graph create(GraphId id, Collection<Block> blocks, Collection<Connection> connections, Collection<Group> groups) {
        return new Graph(GraphId.create(), blocks, connections, groups);
    }


}

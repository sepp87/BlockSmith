package blocksmith.app;

import blocksmith.domain.connection.Connection;
import blocksmith.domain.graph.Graph;
import java.util.ArrayList;

/**
 *
 * @author joost
 */
public class RemoveConnection {

    public Graph execute(Graph graph, Connection connection) {
        var connections = new ArrayList<Connection>();
        connections.remove(connection);
        return new Graph(
                graph.metadata(),
                graph.blocks(),
                connections, 
                graph.groups()
        );
    }
}

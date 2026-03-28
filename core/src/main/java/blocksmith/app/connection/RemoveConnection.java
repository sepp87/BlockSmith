package blocksmith.app.connection;

import blocksmith.domain.connection.Connection;
import blocksmith.domain.graph.Graph;

/**
 *
 * @author joost
 */
public class RemoveConnection {

    public Graph execute(Graph graph, Connection connection) {
        return graph.withoutConnection(connection);
    }
}

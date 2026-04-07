package blocksmith.app.connection;

import blocksmith.domain.block.ArrayBlock;
import blocksmith.domain.connection.Connection;
import blocksmith.domain.graph.Graph;

/**
 *
 * @author joost
 */
public class RemoveConnection {

    public Graph execute(Graph graph, Connection connection) {
        var updatedGraph = graph.withoutConnection(connection);

        var toBlock = graph.block(connection.to().blockId());
        if (toBlock.get() instanceof ArrayBlock toArray) {
            var updatedArray = toArray.withFittedElements(updatedGraph);
            return updatedGraph.withBlock(updatedArray);
        }

        return updatedGraph;
    }
}

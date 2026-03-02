package blocksmith.app.inbound;

import blocksmith.domain.graph.Graph;
import java.util.function.BiConsumer;

/**
 *
 * @author joostmeulenkamp
 */
public interface GraphQuery {

    Graph graphSnapshot();

    void addGraphListener(BiConsumer<Graph, Graph> listener);
}

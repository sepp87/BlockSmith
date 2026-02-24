package blocksmith.app.inbound;

import blocksmith.domain.graph.Graph;
import java.util.function.BiConsumer;

/**
 *
 * @author joostmeulenkamp
 */
public interface GraphQuery {

    Graph currentGraph();

    void setOnGraphUpdated(BiConsumer<Graph, Graph> listener);
}


package blocksmith.app.inbound;

import blocksmith.domain.graph.Graph;
import java.util.function.Consumer;

/**
 *
 * @author joostmeulenkamp
 */
public interface GraphQuery {
    
    Graph currentGraph();
    
    void setOnGraphUpdated(Consumer<Graph> listener);
}

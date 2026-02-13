

package blocksmith.app.outbound;

import blocksmith.domain.graph.Graph;
import java.io.IOException;
import java.nio.file.Path;

/**
 *
 * @author joost
 */
public interface GraphRepo {
    
    Graph load(Path path) throws Exception;
    
    void save(Path path, Graph graph) throws Exception;
}

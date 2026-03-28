

package blocksmith.app.outbound;

import blocksmith.app.GraphDocument;
import java.nio.file.Path;

/**
 *
 * @author joost
 */
public interface GraphRepo {
    
    GraphDocument load(Path path) throws Exception;
    
    void save(Path path, GraphDocument document) throws Exception;
}

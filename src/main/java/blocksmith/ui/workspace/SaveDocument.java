
package blocksmith.ui.workspace;

import blocksmith.app.GraphDocument;
import blocksmith.app.outbound.GraphRepo;
import java.nio.file.Path;

/**
 *
 * @author joost
 */
public class SaveDocument {

    private final GraphRepo repo;
    
    public SaveDocument (GraphRepo repo) {
        this.repo = repo;
    }
    
    public void execute(Path path, GraphDocument document) throws Exception {
        repo.save(path, document);
    }
    
}

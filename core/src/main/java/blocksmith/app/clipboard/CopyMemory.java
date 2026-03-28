
package blocksmith.app.clipboard;

import blocksmith.domain.graph.Graph;
import java.util.Optional;

/**
 *
 * @author joost
 */
public class CopyMemory {

    private Graph copy;
    
    public void add(Graph copy) {
        this.copy = copy;
    }
    
    public Optional<Graph> getCopy() {
        return Optional.ofNullable(copy);
    }
    
   
    
}

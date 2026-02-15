
package blocksmith.app.inbound;

import blocksmith.domain.graph.Graph;

/**
 *
 * @author joostmeulenkamp
 */
public interface GraphQuery {
    
    Graph currentGraph();
    
    boolean hasUndoableState();
    
    boolean hasRedoableState();
    
}

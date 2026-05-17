package blocksmith.app.workspace;

import blocksmith.domain.graph.Graph;

/**
 *
 * @author joost
 */
public class TypeSessionFactory {

    
    public TypeSession create(Graph graph) {
        return new TypeSession(graph);
    }
}

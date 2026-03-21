package blocksmith.exec;

import blocksmith.domain.graph.Graph;

/**
 *
 * @author joost
 */
public class ForgeSessionFactory {

    public ForgeSession create(Graph graph) {
        var engine = new ForgeEngine();
        var state = new ForgeState();
        var invalidator = new ForgeInvalidator();
        var session = new ForgeSession(engine, state, invalidator, graph);
        return session;
    }

}

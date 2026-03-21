package blocksmith.exec;

import blocksmith.domain.graph.Graph;
import blocksmith.domain.graph.GraphDiff;

/**
 *
 * @author joost
 */
public class ForgeSession {

    // ValueFlowEngine 
    // ValueState / FlowState
    // FlowInvalidator
    private final ForgeEngine engine;
    private final ForgeState tempState;
    private final ForgeState state;
    private final ForgeInvalidator invalidator;
    private Graph current;

    public ForgeSession(
            ForgeEngine engine,
            ForgeState state,
            ForgeInvalidator invalidator,
            Graph graph) {

        this.engine = engine;
        this.tempState = new ForgeState();
        this.state = state;
        this.invalidator = invalidator;
        this.current = graph;
    }

    public ForgeState runtimeState() {
        return tempState;
    }

    public void onGraphChanged(Graph oldGraph, Graph newGraph) {
        this.current = newGraph;
        var diff = GraphDiff.compare(oldGraph, newGraph);

//        state.invalidate();
    }

}

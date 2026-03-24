package blocksmith.exec;

import blocksmith.domain.graph.Graph;
import blocksmith.domain.graph.GraphDiff;
import blocksmith.domain.graph.GraphFactory;
import blocksmith.ui.UiApp;

/**
 *
 * @author joost
 */
public class ExecutionSession {

    // ValueFlowEngine 
    // ValueState / FlowState
    // FlowInvalidator
    private final ExecutionEngine engine;
    private final ExecutionState tempState;
    private final ExecutionState state;
    private final ExecutionInvalidator invalidator;
    private Graph current;

    public ExecutionSession(
            ExecutionEngine engine,
            ExecutionState state,
            ExecutionInvalidator invalidator,
            Graph graph) {

        this.engine = engine;
        this.tempState = new ExecutionState();
        this.state = state;
        this.invalidator = invalidator;
        this.current = graph;
        onGraphChanged(GraphFactory.createEmpty(), graph);
    }

    public ExecutionState runtimeState() {
        if(UiApp.USE_EXEC_LAYER) {
            return state;
        }
        return tempState;
    }

    public void onGraphChanged(Graph oldGraph, Graph newGraph) {
        this.current = newGraph;
        var diff = GraphDiff.compare(oldGraph, newGraph);
        invalidator.invalidate(state, oldGraph, newGraph, diff);
        engine.runAll(newGraph, state);
    }

}

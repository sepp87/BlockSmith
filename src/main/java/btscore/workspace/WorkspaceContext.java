package btscore.workspace;

import blocksmith.app.inbound.GraphMutationAndHistory;
import java.util.UUID;

/**
 *
 * @author Joost
 */
public class WorkspaceContext {

    private final String id;
    private final WorkspaceState state;
    private WorkspaceController controller;

    public WorkspaceContext(
            WorkspaceState state
    ) {
        this.id = UUID.randomUUID().toString();
        this.state = state;
//        this.history = history;
    }

    public void attachController(WorkspaceController controller) {
        this.controller = controller;
    }

    public String id() {
        return id;

    }

    public WorkspaceState state() {
        return state;
    }

    public WorkspaceController controller() {
        return controller;
    }

    public WorkspaceModel model() {
        return controller.getModel();
    }

    public WorkspaceView view() {
        return controller.getView();
    }

    public GraphMutationAndHistory graphEditor() {
        return controller.getModel().graphEditor();
    }

}

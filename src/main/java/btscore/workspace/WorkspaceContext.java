package btscore.workspace;

import java.util.UUID;

/**
 *
 * @author Joost
 */
public class WorkspaceContext {

    private final String id;
    private final WorkspaceState state;
    private final WorkspaceHistory history;
    private WorkspaceController controller;

    public WorkspaceContext(
            WorkspaceState state, 
            WorkspaceHistory history
    ) {
        this.id = UUID.randomUUID().toString();
        this.state = state;
        this.history = history;
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
    
    public WorkspaceHistory history() {
        return history;
    }

    public WorkspaceController controller() {
        return controller;
    }

}

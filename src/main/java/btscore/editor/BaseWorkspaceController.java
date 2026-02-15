package btscore.editor;

import btscore.UiApp;
import btscore.workspace.WorkspaceContext;

/**
 *
 * @author Joost
 */
public class BaseWorkspaceController {

    protected WorkspaceContext context;
    private BaseWorkspaceController parent;

    public BaseWorkspaceController(WorkspaceContext context) {
        this.context = context;
    }

    public BaseWorkspaceController(BaseWorkspaceController parent) {
        this.parent = parent;
    }

    public WorkspaceContext context() {
        return context == null ? parent.context(): context;
    }

}

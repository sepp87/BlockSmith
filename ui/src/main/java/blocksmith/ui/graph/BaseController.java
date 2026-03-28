package blocksmith.ui.graph;

import blocksmith.app.workspace.WorkspaceCommandBus;
import blocksmith.ui.workspace.WorkspaceFxHandle;
import blocksmith.app.workspace.WorkspaceSession;

/**
 *
 * @author Joost
 */
public class BaseController {

    protected final WorkspaceCommandBus commands;
    protected final WorkspaceSession session;

    public BaseController( WorkspaceCommandBus commandBus, WorkspaceSession session) {
        this.commands = commandBus;
        this.session = session;
    }

}

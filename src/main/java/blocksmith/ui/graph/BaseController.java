package blocksmith.ui.graph;

import blocksmith.ui.command.WorkspaceCommandBus;
import blocksmith.ui.workspace.FxWorkspaceHandle;
import blocksmith.ui.workspace.WorkspaceSession;

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

package btscore.graph;

import btscore.command.WorkspaceCommandBus;
import btscore.workspace.WorkspaceContext;
import btscore.workspace.WorkspaceSession;

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

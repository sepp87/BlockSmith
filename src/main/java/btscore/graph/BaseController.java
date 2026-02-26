package btscore.graph;

import btscore.command.CommandDispatcher;
import btscore.command.CommandFactory;
import btscore.workspace.WorkspaceContext;
import btscore.workspace.WorkspaceState;

/**
 *
 * @author Joost
 */
public class BaseController {

    protected final CommandDispatcher actionManager;
    protected final CommandFactory commandFactory;
    protected final WorkspaceContext workspaceContext;

    public BaseController(CommandDispatcher actionManager, CommandFactory commandFactory, WorkspaceContext workspaceContext) {
        this.actionManager = actionManager;
        this.commandFactory = commandFactory;
        this.workspaceContext = workspaceContext;
    }

}

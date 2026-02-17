package btscore.editor;

import btscore.editor.context.ActionManager;
import btscore.editor.context.CommandFactory;
import btscore.workspace.WorkspaceContext;
import btscore.workspace.WorkspaceState;

/**
 *
 * @author Joost
 */
public class BaseController {

    protected final ActionManager actionManager;
    protected final CommandFactory commandFactory;
    protected final WorkspaceContext workspaceContext;

    public BaseController(ActionManager actionManager, CommandFactory commandFactory, WorkspaceContext workspaceContext) {
        this.actionManager = actionManager;
        this.commandFactory = commandFactory;
        this.workspaceContext = workspaceContext;
    }

}

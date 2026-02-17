package btscore.editor.commands;

import btscore.editor.context.Command;
import btscore.editor.context.EditorContext;
import btscore.editor.context.ResetHistoryCommand;
import btscore.workspace.WorkspaceContext;
import btscore.workspace.WorkspaceModel;

/**
 *
 * @author Joost
 */
public class NewFileCommand implements Command, ResetHistoryCommand {

    private final WorkspaceModel workspaceModel;
    private final EditorContext context;

    public NewFileCommand(WorkspaceModel workspaceModel, EditorContext context) {
        this.workspaceModel = workspaceModel;
        this.context = context;
    }

    @Override
    public boolean execute(WorkspaceContext context) {
        workspaceModel.reset();
        // Session Manager > create new Workspace Session
        return true;

    }

}

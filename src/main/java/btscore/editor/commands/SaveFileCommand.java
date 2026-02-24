package btscore.editor.commands;

import btscore.Config;
import btscore.editor.context.Command;
import btscore.workspace.WorkspaceModel;
import btscore.editor.context.MarkSavedCommand;
import btscore.workspace.WorkspaceContext;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Joost
 */
public class SaveFileCommand implements Command, MarkSavedCommand {

    private final WorkspaceModel workspaceModel;

    public SaveFileCommand(WorkspaceModel workspaceModel) {
        this.workspaceModel = workspaceModel;
    }

    @Override
    public boolean execute(WorkspaceContext context) {

        var path = workspaceModel.documentPath().orElse(null);
        if (path != null) {
            try {
                workspaceModel.saveDocument(path);
                Config.setLastOpenedDirectory(path.toFile());

            } catch (Exception ex) {
                Logger.getLogger(SaveFileCommand.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return new SaveAsFileCommand(workspaceModel).execute(context);
    }

}

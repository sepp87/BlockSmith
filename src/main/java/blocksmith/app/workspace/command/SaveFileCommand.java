package blocksmith.app.workspace.command;

import blocksmith.ui.Config;
import blocksmith.app.command.Command;
import blocksmith.app.workspace.WorkspaceSession;
import blocksmith.app.command.WorkspaceCommand;
import blocksmith.ui.workspace.command.SaveAsFileCommand;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Joost
 */
public class SaveFileCommand implements WorkspaceCommand {

    private final WorkspaceSession workspaceModel;

    public SaveFileCommand(WorkspaceSession workspaceModel) {
        this.workspaceModel = workspaceModel;
    }

    @Override
    public boolean execute() {

        if(workspaceModel.isSaved()) {
            return true;
        }
        
        var path = workspaceModel.documentPath().orElse(null);
        if (path != null) {
            try {
                workspaceModel.saveDocument(path);
                Config.setLastOpenedDirectory(path.toFile());
                return true;
                
            } catch (Exception ex) {
                Logger.getLogger(SaveFileCommand.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return new SaveAsFileCommand(workspaceModel).execute();
    }

}

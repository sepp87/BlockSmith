package blocksmith.app.workspace.command;

import blocksmith.Config;
import blocksmith.app.workspace.WorkspaceSession;
import blocksmith.app.command.WorkspaceCommand;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Joost
 */
public class SaveFileCommand implements WorkspaceCommand {

    private final WorkspaceSession workspace;

    public SaveFileCommand(WorkspaceSession workspaceModel) {
        this.workspace = workspaceModel;
    }

    @Override
    public boolean execute() {

        if(workspace.isSaved()) {
            return true;
        }
        
        var path = workspace.documentPath().orElse(null);
        if (path != null) {
            try {
                workspace.saveDocument(path);
                Config.setLastOpenedDirectory(path.toFile());
                return true;
                
            } catch (Exception ex) {
                Logger.getLogger(SaveFileCommand.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

}

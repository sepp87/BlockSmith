package blocksmith.ui.command.workspace;

import blocksmith.ui.Config;
import blocksmith.ui.command.Command;
import blocksmith.ui.workspace.WorkspaceSession;
import blocksmith.ui.command.WorkspaceCommand;
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

            } catch (Exception ex) {
                Logger.getLogger(SaveFileCommand.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return new SaveAsFileCommand(workspaceModel).execute();
    }

}

package blocksmith.ui.workspace.command;

import blocksmith.app.workspace.WorkspaceSession;
import blocksmith.app.command.WorkspaceCommand;
import blocksmith.ui.UserPrefsService;
import blocksmith.ui.workspace.command.SaveAsFileCommand;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Joost
 */
public class SaveFileUiCommand implements WorkspaceCommand {

    private final WorkspaceSession workspace;
    private final UserPrefsService userPrefsService;

    public SaveFileUiCommand(WorkspaceSession workspaceModel, UserPrefsService userPrefsService) {
        this.workspace = workspaceModel;
        this.userPrefsService = userPrefsService;
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
                userPrefsService.setLastOpenedDir(path);
                userPrefsService.setInitialDocument(path);
                return true;
                
            } catch (Exception ex) {
                Logger.getLogger(SaveFileUiCommand.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return new SaveAsFileCommand(workspace, userPrefsService).execute();
    }

}

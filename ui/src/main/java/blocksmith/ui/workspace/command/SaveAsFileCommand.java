package blocksmith.ui.workspace.command;

import java.io.File;
import javafx.stage.FileChooser;
import blocksmith.ui.UiApp;
import blocksmith.app.workspace.WorkspaceSession;
import blocksmith.app.command.WorkspaceCommand;
import blocksmith.infra.xml.DocumentFormat;
import blocksmith.ui.UserPrefsService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joostmeulenkamp
 */
public class SaveAsFileCommand implements WorkspaceCommand {

    private final WorkspaceSession workspaceSession;
    private final UserPrefsService userPrefsService;

    public SaveAsFileCommand(WorkspaceSession workspaceSession, UserPrefsService userPrefsService) {
        this.workspaceSession = workspaceSession;
        this.userPrefsService = userPrefsService;
    }

    @Override
    public boolean execute() {

        FileChooser chooser = new FileChooser();
        userPrefsService.getLastOpenedDir().ifPresent(dir -> chooser.setInitialDirectory(dir.toFile()));

        chooser.setTitle("Save as " + DocumentFormat.FILE_EXTENSION + " file");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(DocumentFormat.NAMESPACE, "*" + DocumentFormat.FILE_EXTENSION));
        File file = chooser.showSaveDialog(UiApp.getStage());

        if (file != null) {
            var path = file.toPath();
            userPrefsService.setLastOpenedDir(path);
            userPrefsService.setInitialDocument(path);

            try {
                workspaceSession.saveDocument(path);
            } catch (Exception ex) {
                Logger.getLogger(SaveAsFileCommand.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return true;

    }

}

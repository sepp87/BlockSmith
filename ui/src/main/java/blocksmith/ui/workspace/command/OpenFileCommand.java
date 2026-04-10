package blocksmith.ui.workspace.command;

import blocksmith.app.workspace.WorkspaceLifecycle;
import java.io.File;
import javafx.stage.FileChooser;
import blocksmith.ui.UiApp;
import blocksmith.app.command.AppCommand;
import blocksmith.infra.xml.DocumentFormat;
import blocksmith.ui.UserPrefsService;

/**
 *
 * @author Joost
 */
public class OpenFileCommand implements AppCommand {

    private final WorkspaceLifecycle workspaces;
    private final UserPrefsService userPrefsService;

    public OpenFileCommand(WorkspaceLifecycle workspaces, UserPrefsService userPrefsService) {
        this.workspaces = workspaces;
        this.userPrefsService = userPrefsService;
    }

    @Override
    public boolean execute() {
        //Open File
        FileChooser chooser = new FileChooser();
        userPrefsService.getLastOpenedDir().ifPresent(dir -> chooser.setInitialDirectory(dir.toFile()));
        
        chooser.setTitle("Open a " + DocumentFormat.FILE_EXTENSION + " file");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(DocumentFormat.NAMESPACE, "*." + DocumentFormat.FILE_EXTENSION));

        File file = chooser.showOpenDialog(UiApp.getStage());

        if (file == null) {
            return false;
        }
        
        var path = file.toPath();

        userPrefsService.setLastOpenedDir(path);
        userPrefsService.setInitialDocument(path);
        
        //Load file
        workspaces.openDocument(path);

        return true;

    }

}

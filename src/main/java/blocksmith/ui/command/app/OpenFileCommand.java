package blocksmith.ui.command.app;

import blocksmith.app.outbound.GraphRepo;
import blocksmith.app.workspace.WorkspaceLifecycle;
import java.io.File;
import javafx.stage.FileChooser;
import blocksmith.ui.UiApp;
import blocksmith.ui.Config;
import blocksmith.ui.command.AppCommand;
import blocksmith.ui.workspace.WorkspaceSession;

/**
 *
 * @author Joost
 */
public class OpenFileCommand implements AppCommand {

    private final WorkspaceLifecycle workspaces;

    public OpenFileCommand(WorkspaceLifecycle workspaces) {
        this.workspaces = workspaces;
    }

    @Override
    public boolean execute() {
        //Open File
        FileChooser chooser = new FileChooser();
        File lastOpenedDirectory = Config.getLastOpenedDirectory();
        if (lastOpenedDirectory != null) {
            chooser.setInitialDirectory(lastOpenedDirectory);
        }
        chooser.setTitle("Open a ." + Config.XML_FILE_EXTENSION + " file");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(Config.XML_FILE_EXTENSION, "*." + Config.XML_FILE_EXTENSION));

        File file = chooser.showOpenDialog(UiApp.getStage());

        if (file == null) {
            return false;
        }

        Config.setLastOpenedDirectory(file);

        //Load file
        workspaces.openDocument(file.toPath());

        return true;

    }

}

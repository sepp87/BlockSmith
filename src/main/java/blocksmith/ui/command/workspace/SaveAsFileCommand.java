package blocksmith.ui.command.workspace;

import java.io.File;
import javafx.stage.FileChooser;
import blocksmith.ui.UiApp;
import blocksmith.ui.Config;
import blocksmith.app.workspace.Command;
import blocksmith.app.workspace.WorkspaceSession;
import blocksmith.app.workspace.WorkspaceCommand;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joostmeulenkamp
 */
public class SaveAsFileCommand implements WorkspaceCommand {

    private final WorkspaceSession workspaceModel;

    public SaveAsFileCommand(WorkspaceSession workspaceModel) {
        this.workspaceModel = workspaceModel;
    }

    @Override
    public boolean execute() {

        FileChooser chooser = new FileChooser();
        File lastOpenedDirectory = Config.getLastOpenedDirectory();
        if (lastOpenedDirectory != null) {
            chooser.setInitialDirectory(lastOpenedDirectory);
        }
        chooser.setTitle("Save as ." + Config.XML_FILE_EXTENSION + " file");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(Config.XML_FILE_EXTENSION, "*." + Config.XML_FILE_EXTENSION));
        File file = chooser.showSaveDialog(UiApp.getStage());

        if (file != null) {
            Config.setLastOpenedDirectory(file);

            try {
                workspaceModel.saveDocument(file.toPath());
            } catch (Exception ex) {
                Logger.getLogger(SaveAsFileCommand.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return true;

    }

}

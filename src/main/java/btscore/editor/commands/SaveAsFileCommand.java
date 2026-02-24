package btscore.editor.commands;

import java.io.File;
import javafx.stage.FileChooser;
import btscore.UiApp;
import btscore.Config;
import btscore.editor.context.Command;
import btscore.workspace.WorkspaceModel;
import btscore.editor.context.MarkSavedCommand;
import btscore.workspace.WorkspaceContext;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joostmeulenkamp
 */
public class SaveAsFileCommand implements Command, MarkSavedCommand {

    private final WorkspaceModel workspaceModel;

    public SaveAsFileCommand(WorkspaceModel workspaceModel) {
        this.workspaceModel = workspaceModel;
    }

    @Override
    public boolean execute(WorkspaceContext context) {

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

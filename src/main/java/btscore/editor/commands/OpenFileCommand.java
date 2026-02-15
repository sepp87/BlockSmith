package btscore.editor.commands;

import java.io.File;
import javafx.stage.FileChooser;
import btscore.UiApp;
import btscore.Config;
import btscore.editor.context.Command;
import btscore.graph.io.GraphLoader;
import btscore.editor.context.ResetHistoryCommand;
import btscore.editor.context.MarkSavedCommand;
import btscore.workspace.WorkspaceContext;
import btscore.workspace.WorkspaceModel;

/**
 *
 * @author Joost
 */
public class OpenFileCommand implements Command, ResetHistoryCommand, MarkSavedCommand {

    private final WorkspaceModel workspaceModel;

    public OpenFileCommand(WorkspaceModel workspaceModel) {
        this.workspaceModel = workspaceModel;
    }

    @Override
    public boolean execute(WorkspaceContext context) {
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

        //Clear the workspace
        workspaceModel.reset();

        //Load file
        GraphLoader.deserialize(file, workspaceModel);
        return true;

    }

}

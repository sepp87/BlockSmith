package btscore.command.app;

import blocksmith.app.outbound.GraphRepo;
import java.io.File;
import javafx.stage.FileChooser;
import btscore.UiApp;
import btscore.Config;
import btscore.command.AppCommand;
import btscore.graph.io.GraphLoader;
import btscore.workspace.WorkspaceModel;

/**
 *
 * @author Joost
 */
public class OpenFileCommand implements AppCommand {

    private final WorkspaceModel workspaceModel;
    private final GraphRepo graphRepo;

    public OpenFileCommand(WorkspaceModel workspaceModel, GraphRepo graphRepo) {
        this.workspaceModel = workspaceModel;
        this.graphRepo = graphRepo; 
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

        //Clear the workspace
        workspaceModel.reset();

        //Load file
        GraphLoader.deserialize(file, workspaceModel);
        return true;

    }

}

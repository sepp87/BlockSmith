package btscore.editor.commands;

import blocksmith.app.outbound.GraphRepo;
import java.io.File;
import javafx.stage.FileChooser;
import btscore.UiApp;
import btscore.Config;
import btscore.Launcher;
import btscore.graph.io.GraphSaver;
import btscore.editor.context.Command;
import btscore.workspace.WorkspaceModel;
import btscore.editor.context.MarkSavedCommand;
import btscore.graph.io.GraphSaverV2;
import btscore.workspace.WorkspaceContext;

/**
 *
 * @author joostmeulenkamp
 */
public class SaveAsFileCommand implements Command, MarkSavedCommand {

    private final WorkspaceModel workspaceModel;
    private final GraphRepo graphRepo;

    public SaveAsFileCommand(WorkspaceModel workspaceModel, GraphRepo graphRepo) {
        this.workspaceModel = workspaceModel;
        this.graphRepo = graphRepo;
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
            if (Launcher.GRAPH_LOADER_V2) {
                GraphSaverV2.serialize(file, workspaceModel);
            } else {
                GraphSaver.serialize(file, workspaceModel);

            }
        }
        return true;

    }

}

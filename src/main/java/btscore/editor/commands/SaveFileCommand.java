package btscore.editor.commands;

import blocksmith.app.outbound.GraphRepo;
import java.io.File;
import btscore.Config;
import btscore.editor.context.Command;
import btscore.graph.io.GraphSaver;
import btscore.workspace.WorkspaceModel;
import btscore.editor.context.MarkSavedCommand;
import btscore.workspace.WorkspaceContext;

/**
 *
 * @author Joost
 */
public class SaveFileCommand implements Command, MarkSavedCommand {

    private final WorkspaceModel workspaceModel;
    private final GraphRepo graphRepo;

    public SaveFileCommand(WorkspaceModel workspaceModel, GraphRepo graphRepo) {
        this.workspaceModel = workspaceModel;
        this.graphRepo = graphRepo;
    }

    @Override
    public boolean execute(WorkspaceContext context) {

        File file = workspaceModel.fileProperty().get();

        if (file != null) {
            Config.setLastOpenedDirectory(file);
            GraphSaver.serialize(file, workspaceModel);
        } else {
            new SaveAsFileCommand(workspaceModel, graphRepo).execute(context);
        }
        return true;

    }

}

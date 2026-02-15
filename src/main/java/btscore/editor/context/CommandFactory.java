package btscore.editor.context;

import blocksmith.ui.BlockModelFactory;
import btscore.editor.commands.DeselectAllBlocksCommand;
import btscore.editor.commands_done.AlignBottomCommand;
import btscore.editor.commands_done.AlignHorizontallyCommand;
import btscore.editor.commands_done.AlignLeftCommand;
import btscore.editor.commands_done.AlignRightCommand;
import btscore.editor.commands_done.AlignTopCommand;
import btscore.editor.commands_done.AlignVerticallyCommand;
import btscore.editor.commands_todo.CopyBlocksCommand;
import btscore.editor.commands_done.AddGroupCommand;
import btscore.editor.commands.HelpCommand;
import btscore.editor.commands.NewFileCommand;
import btscore.editor.commands.OpenFileCommand;
import btscore.editor.commands.RectangleSelectCommand;
import btscore.editor.commands_todo.PasteBlocksCommand;
import btscore.editor.commands.ReloadPluginsCommand;
import btscore.editor.commands_done.RemoveBlocksCommand;
import btscore.editor.commands.SaveAsFileCommand;
import btscore.editor.commands.SaveFileCommand;
import btscore.editor.commands.SelectAllBlocksCommand;
import btscore.editor.commands.UpdateSelectionCommand;
import btscore.editor.commands_done.AddBlockCommand;
import btscore.editor.commands_done.AddConnectionCommand;
import btscore.editor.commands_done.RemoveConnectionCommand;
import btscore.editor.commands_done.RemoveGroupCommand;
import btscore.editor.commands_todo.ZoomCommand;
import btscore.editor.commands_todo.ZoomInCommand;
import btscore.editor.commands_todo.ZoomOutCommand;
import btscore.editor.commands_todo.ZoomToFitCommand;
import btscore.graph.block.BlockController;
import btscore.graph.connection.ConnectionModel;
import btscore.graph.group.BlockGroupModel;
import btscore.graph.port.PortModel;
import javafx.geometry.Point2D;

/**
 *
 * @author joostmeulenkamp
 */
public class CommandFactory {

    private final EditorContext context;
    private final BlockModelFactory blockModelFactory;

    public CommandFactory(EditorContext context, BlockModelFactory blockModelFactory) {
        this.context = context;
        this.blockModelFactory = blockModelFactory;
    }

    public Command createCommand(Command.Id id) {
        var workspace = context.activeWorkspace();
        var workspaceModel = workspace.model();
        var workspaceController = workspace.controller();
        var session = workspace.session();

        return switch (id) {
            case NEW_FILE ->
                new NewFileCommand(workspaceModel);
            case OPEN_FILE ->
                new OpenFileCommand(workspaceModel);
            case SAVE_FILE ->
                new SaveFileCommand(workspaceModel);
            case SAVE_AS_FILE ->
                new SaveAsFileCommand(workspaceModel);
            case COPY_BLOCKS ->
                new CopyBlocksCommand(workspaceController);
            case PASTE_BLOCKS ->
                new PasteBlocksCommand(workspaceController, workspaceModel, context.getMousePositionOnWorkspace());
            case REMOVE_BLOCKS ->
                new RemoveBlocksCommand(workspaceController, session);
            case SELECT_ALL_BLOCKS ->
                new SelectAllBlocksCommand(workspaceController);
            case DESELECT_ALL_BLOCKS ->
                new DeselectAllBlocksCommand(workspaceController);
            case ADD_GROUP ->
                new AddGroupCommand(workspaceController, workspaceModel, session);
            case ALIGN_LEFT ->
                new AlignLeftCommand(workspaceController, session);
            case ALIGN_VERTICALLY ->
                new AlignVerticallyCommand(workspaceController, session);
            case ALIGN_RIGHT ->
                new AlignRightCommand(workspaceController, session);
            case ALIGN_TOP ->
                new AlignTopCommand(workspaceController, session);
            case ALIGN_HORIZONTALLY ->
                new AlignHorizontallyCommand(workspaceController, session);
            case ALIGN_BOTTOM ->
                new AlignBottomCommand(workspaceController, session);
            case ZOOM_TO_FIT ->
                new ZoomToFitCommand(workspaceController);
            case ZOOM_IN ->
                new ZoomInCommand(workspaceController);
            case ZOOM_OUT ->
                new ZoomOutCommand(workspaceController);
            case RELOAD_PLUGINS ->
                new ReloadPluginsCommand();
            case HELP ->
                new HelpCommand();
        };
    }

    public Command createAddBlockCommand(String type, Point2D location) {
        var workspace = context.activeWorkspace();
        var workspaceModel = workspace.model();
        var session = workspace.session();
        return new AddBlockCommand(session, blockModelFactory, workspaceModel, type, location);
    }

    public Command createRemoveBlocksCommand() {
        var workspace = context.activeWorkspace();
        var workspaceController = workspace.controller();
        var session = workspace.session();
        return new RemoveBlocksCommand(workspaceController, session);
    }

    public Command createAddConnectionCommand(PortModel from, PortModel to) {
        var workspace = context.activeWorkspace();
        var workspaceModel = workspace.model();
        var session = workspace.session();
        return new AddConnectionCommand(workspaceModel, from, to, session);
    }

    public Command createRemoveConnectionCommand(ConnectionModel connection) {
        var workspace = context.activeWorkspace();
        var workspaceModel = workspace.model();
        var session = workspace.session();
        return new RemoveConnectionCommand(workspaceModel, connection, session);
    }

    public Command createAddGroupCommand() {
        var workspace = context.activeWorkspace();
        var workspaceModel = workspace.model();
        var workspaceController = workspace.controller();
        var session = workspace.session();
        return new AddGroupCommand(workspaceController, workspaceModel, session);
    }

    public Command createRemoveGroupCommand(BlockGroupModel group) {
        var workspace = context.activeWorkspace();
        var workspaceModel = workspace.model();
        var session = workspace.session();
        return new RemoveGroupCommand(workspaceModel, group, session);
    }

    public Command createZoomCommand(double newScale, Point2D pivotPoint) {
        var workspace = context.activeWorkspace();
        var workspaceController = workspace.controller();
        return new ZoomCommand(workspaceController, newScale, pivotPoint);
    }

    public Command createRectangleSelectCommand(Point2D selectionMin, Point2D selectionMax) {
        var workspace = context.activeWorkspace();
        var workspaceController = workspace.controller();
        return new RectangleSelectCommand(workspaceController, selectionMin, selectionMax);
    }

    public Command createUpdateSelectionCommand(BlockController block, boolean isModifierDown) {
        var workspace = context.activeWorkspace();
        var workspaceController = workspace.controller();
        return new UpdateSelectionCommand(workspaceController, block, isModifierDown);
    }

}

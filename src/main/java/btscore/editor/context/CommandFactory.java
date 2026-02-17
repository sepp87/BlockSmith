package btscore.editor.context;

import blocksmith.app.outbound.GraphRepo;
import blocksmith.ui.BlockModelFactory;
import btscore.editor.commands_done.DeselectAllBlocksCommand;
import btscore.editor.commands_done.AlignBottomCommand;
import btscore.editor.commands_done.AlignHorizontallyCommand;
import btscore.editor.commands_done.AlignLeftCommand;
import btscore.editor.commands_done.AlignRightCommand;
import btscore.editor.commands_done.AlignTopCommand;
import btscore.editor.commands_done.AlignVerticallyCommand;
import btscore.editor.commands_todo.CopyBlocksCommand;
import btscore.editor.commands_done.AddGroupCommand;
import btscore.editor.commands_done.HelpCommand;
import btscore.editor.commands.NewFileCommand;
import btscore.editor.commands.OpenFileCommand;
import btscore.editor.commands_done.RectangleSelectCommand;
import btscore.editor.commands_todo.PasteBlocksCommand;
import btscore.editor.commands_done.ReloadPluginsCommand;
import btscore.editor.commands_done.RemoveBlocksCommand;
import btscore.editor.commands.SaveAsFileCommand;
import btscore.editor.commands.SaveFileCommand;
import btscore.editor.commands_done.SelectAllBlocksCommand;
import btscore.editor.commands_done.UpdateSelectionCommand;
import btscore.editor.commands_done.AddBlockCommand;
import btscore.editor.commands_done.AddConnectionCommand;
import btscore.editor.commands_done.RemoveConnectionCommand;
import btscore.editor.commands_done.RemoveGroupCommand;
import btscore.editor.commands_done.ZoomCommand;
import btscore.editor.commands_done.ZoomInCommand;
import btscore.editor.commands_done.ZoomOutCommand;
import btscore.editor.commands_done.ZoomToFitCommand;
import btscore.editor.commands_done.MoveBlocksCommand;
import btscore.editor.commands_todo.ResizeBlockCommand;
import btscore.graph.block.BlockController;
import btscore.graph.connection.ConnectionModel;
import btscore.graph.group.BlockGroupModel;
import btscore.graph.port.PortModel;
import java.util.Collection;
import javafx.geometry.Point2D;

/**
 *
 * @author joostmeulenkamp
 */
public class CommandFactory {

    private final EditorContext context;
    private final BlockModelFactory blockModelFactory;
    private final GraphRepo graphRepo;

    public CommandFactory(EditorContext context, BlockModelFactory blockModelFactory, GraphRepo graphRepo) {
        this.context = context;
        this.blockModelFactory = blockModelFactory;
        this.graphRepo = graphRepo;
    }

    public Command createCommand(Command.Id id) {
        var workspace = context.activeWorkspace();
        var workspaceModel = workspace.controller().getModel();
        var workspaceController = workspace.controller();

        return switch (id) {
            case NEW_FILE ->
                new NewFileCommand(workspaceModel, context);
            case OPEN_FILE ->
                new OpenFileCommand(workspaceModel, graphRepo, context);
            case SAVE_FILE ->
                new SaveFileCommand(workspaceModel, graphRepo);
            case SAVE_AS_FILE ->
                new SaveAsFileCommand(workspaceModel, graphRepo);
            case COPY_BLOCKS ->
                new CopyBlocksCommand(workspaceController);
            case PASTE_BLOCKS ->
                new PasteBlocksCommand(workspaceController, workspaceModel, context.getMousePositionOnWorkspace());
            case REMOVE_BLOCKS ->
                new RemoveBlocksCommand(workspaceController, workspaceModel);
            case SELECT_ALL_BLOCKS ->
                new SelectAllBlocksCommand(workspaceController);
            case DESELECT_ALL_BLOCKS ->
                new DeselectAllBlocksCommand(workspaceController);
            case ADD_GROUP ->
                new AddGroupCommand(workspaceController, workspaceModel);
            case ALIGN_LEFT ->
                new AlignLeftCommand(workspaceController, workspaceModel);
            case ALIGN_VERTICALLY ->
                new AlignVerticallyCommand(workspaceController, workspaceModel);
            case ALIGN_RIGHT ->
                new AlignRightCommand(workspaceController, workspaceModel);
            case ALIGN_TOP ->
                new AlignTopCommand(workspaceController, workspaceModel);
            case ALIGN_HORIZONTALLY ->
                new AlignHorizontallyCommand(workspaceController, workspaceModel);
            case ALIGN_BOTTOM ->
                new AlignBottomCommand(workspaceController, workspaceModel);
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
        var workspaceModel = workspace.controller().getModel();
        return new AddBlockCommand(blockModelFactory, workspaceModel, type, location);
    }

    public Command createRemoveBlocksCommand() {
        var workspace = context.activeWorkspace();
        var workspaceController = workspace.controller();
        var workspaceModel = workspace.controller().getModel();
        return new RemoveBlocksCommand(workspaceController, workspaceModel);
    }

    public Command createAddConnectionCommand(PortModel from, PortModel to) {
        var workspace = context.activeWorkspace();
        var workspaceModel = workspace.controller().getModel();
        return new AddConnectionCommand(workspaceModel, from, to);
    }

    public Command createRemoveConnectionCommand(ConnectionModel connection) {
        var workspace = context.activeWorkspace();
        var workspaceModel = workspace.controller().getModel();
        return new RemoveConnectionCommand(workspaceModel, connection);
    }

    public Command createAddGroupCommand() {
        var workspace = context.activeWorkspace();
        var workspaceModel = workspace.controller().getModel();
        var workspaceController = workspace.controller();
        return new AddGroupCommand(workspaceController, workspaceModel);
    }

    public Command createRemoveGroupCommand(BlockGroupModel group) {
        var workspace = context.activeWorkspace();
        var workspaceModel = workspace.controller().getModel();
        return new RemoveGroupCommand(workspaceModel, group);
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
    
    public Command createMoveBlocksCommand(Collection<BlockController> blocks, Point2D delta) {
        var workspace = context.activeWorkspace();
        var workspaceModel = workspace.controller().getModel();
        return new MoveBlocksCommand(workspaceModel, blocks, delta);
    }

    public Command createResizeBlockCommand(BlockController blockController, double width, double height) {
        var workspace = context.activeWorkspace();
        var workspaceModel = workspace.controller().getModel();
        return new ResizeBlockCommand(workspaceModel, blockController, width, height);
    }
    
}

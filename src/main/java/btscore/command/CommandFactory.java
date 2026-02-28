package btscore.command;

import btscore.command.Command;
import blocksmith.app.outbound.GraphRepo;
import blocksmith.ui.BlockModelFactory;
import btscore.command.workspace.DeselectAllBlocksCommand;
import btscore.command.workspace.AlignBottomCommand;
import btscore.command.workspace.AlignHorizontallyCommand;
import btscore.command.workspace.AlignLeftCommand;
import btscore.command.workspace.AlignRightCommand;
import btscore.command.workspace.AlignTopCommand;
import btscore.command.workspace.AlignVerticallyCommand;
import btscore.command.workspace.CopyBlocksCommand;
import btscore.command.workspace.AddGroupCommand;
import btscore.command.app.HelpCommand;
import btscore.command.app.NewFileCommand;
import btscore.command.app.OpenFileCommand;
import btscore.command.workspace.RectangleSelectCommand;
import btscore.command.workspace.PasteBlocksCommand;
import btscore.command.app.ReloadPluginsCommand;
import btscore.command.workspace.RemoveBlocksCommand;
import btscore.command.workspace.SaveAsFileCommand;
import btscore.command.workspace.SaveFileCommand;
import btscore.command.workspace.SelectAllBlocksCommand;
import btscore.command.workspace.UpdateSelectionCommand;
import btscore.command.workspace.AddBlockCommand;
import btscore.command.workspace.AddConnectionCommand;
import btscore.command.workspace.RemoveConnectionCommand;
import btscore.command.workspace.RemoveGroupCommand;
import btscore.command.workspace.ZoomCommand;
import btscore.command.workspace.ZoomInCommand;
import btscore.command.workspace.ZoomOutCommand;
import btscore.command.workspace.ZoomToFitCommand;
import btscore.command.workspace.MoveBlocksCommand;
import btscore.command.workspace.ResizeBlockCommand;
import btscore.editor.EditorContext;
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
                new NewFileCommand(workspaceModel);
            case OPEN_FILE ->
                new OpenFileCommand(workspaceModel, graphRepo);
            case SAVE_FILE ->
                new SaveFileCommand(workspaceModel);
            case SAVE_AS_FILE ->
                new SaveAsFileCommand(workspaceModel);
            case COPY_BLOCKS ->
                new CopyBlocksCommand(workspaceModel);
            case PASTE_BLOCKS ->
                new PasteBlocksCommand(workspaceController, workspaceModel, context.getMousePositionOnWorkspace());
            case REMOVE_BLOCKS ->
                new RemoveBlocksCommand(workspaceController, workspaceModel);
            case SELECT_ALL_BLOCKS ->
                new SelectAllBlocksCommand(workspaceController);
            case DESELECT_ALL_BLOCKS ->
                new DeselectAllBlocksCommand(workspaceController);
            case ADD_GROUP ->
                new AddGroupCommand( workspaceModel);
            case ALIGN_LEFT ->
                new AlignLeftCommand(workspaceModel);
            case ALIGN_VERTICALLY ->
                new AlignVerticallyCommand( workspaceModel);
            case ALIGN_RIGHT ->
                new AlignRightCommand(workspaceModel);
            case ALIGN_TOP ->
                new AlignTopCommand(workspaceModel);
            case ALIGN_HORIZONTALLY ->
                new AlignHorizontallyCommand(workspaceModel);
            case ALIGN_BOTTOM ->
                new AlignBottomCommand(workspaceModel);
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
        return new AddGroupCommand(workspaceModel);
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

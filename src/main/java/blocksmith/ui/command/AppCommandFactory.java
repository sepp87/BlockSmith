package blocksmith.ui.command;

import blocksmith.app.workspace.Command;
import blocksmith.app.outbound.GraphRepo;
import blocksmith.app.workspace.WorkspaceLifecycle;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.Connection;
import blocksmith.domain.connection.PortRef;
import blocksmith.domain.group.GroupId;
import blocksmith.app.block.command.DeselectAllBlocksCommand;
import blocksmith.ui.command.workspace.AlignBottomCommand;
import blocksmith.ui.command.workspace.AlignHorizontallyCommand;
import blocksmith.ui.command.workspace.AlignLeftCommand;
import blocksmith.ui.command.workspace.AlignRightCommand;
import blocksmith.ui.command.workspace.AlignTopCommand;
import blocksmith.ui.command.workspace.AlignVerticallyCommand;
import blocksmith.app.block.command.CopyBlocksCommand;
import blocksmith.app.group.command.AddGroupCommand;
import blocksmith.ui.command.app.HelpCommand;
import blocksmith.ui.command.app.NewFileCommand;
import blocksmith.ui.command.app.OpenFileCommand;
import blocksmith.ui.command.workspace.RectangleSelectCommand;
import blocksmith.app.block.command.PasteBlocksCommand;
import blocksmith.ui.command.app.ReloadPluginsCommand;
import blocksmith.app.block.command.RemoveBlocksCommand;
import blocksmith.ui.command.workspace.SaveAsFileCommand;
import blocksmith.ui.command.workspace.SaveFileCommand;
import blocksmith.app.block.command.SelectAllBlocksCommand;
import blocksmith.app.block.command.UpdateSelectionCommand;
import blocksmith.app.block.command.AddBlockCommand;
import blocksmith.app.connection.command.AddConnectionCommand;
import blocksmith.app.connection.command.RemoveConnectionCommand;
import blocksmith.app.group.command.RemoveGroupCommand;
import blocksmith.ui.command.workspace.ZoomCommand;
import blocksmith.ui.command.workspace.ZoomInCommand;
import blocksmith.ui.command.workspace.ZoomOutCommand;
import blocksmith.ui.command.workspace.ZoomToFitCommand;
import blocksmith.app.block.command.MoveBlocksCommand;
import blocksmith.app.block.command.RenameBlockCommand;
import blocksmith.app.block.command.ResizeBlockCommand;
import blocksmith.app.block.command.UpdateParamValueCommand;
import blocksmith.ui.workspace.WorkspaceFxRegistry;
import java.util.Collection;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;

/**
 *
 * @author joostmeulenkamp
 */
public class AppCommandFactory {

    private final WorkspaceLifecycle workspaces;
    private final WorkspaceFxRegistry registry;
    private final GraphRepo graphRepo;

    public AppCommandFactory(WorkspaceLifecycle workspaces, WorkspaceFxRegistry registry, GraphRepo graphRepo) {
        this.workspaces = workspaces;
        this.registry = registry;
        this.graphRepo = graphRepo;
    }

    public Command createCommand(Command.Id id) {
        var workspace = registry.active();
        var session = workspace.session();
        var zoom = workspace.zoom();
        var alignmemt = workspace.alignment();

        return switch (id) {
            case NEW_FILE ->
                new NewFileCommand(workspaces);
            case OPEN_FILE ->
                new OpenFileCommand(workspaces);
            case SAVE_FILE ->
                new SaveFileCommand(session);
            case SAVE_AS_FILE ->
                new SaveAsFileCommand(session);
            case COPY_BLOCKS ->
                new CopyBlocksCommand(session);
            case PASTE_BLOCKS ->
                new PasteBlocksCommand(session, registry.getMousePositionOnWorkspace());
            case REMOVE_BLOCKS ->
                new RemoveBlocksCommand(session);
            case SELECT_ALL_BLOCKS ->
                new SelectAllBlocksCommand(session);
            case DESELECT_ALL_BLOCKS ->
                new DeselectAllBlocksCommand(session);
            case ADD_GROUP ->
                new AddGroupCommand(session);
            case ALIGN_LEFT ->
                new AlignLeftCommand(alignmemt);
            case ALIGN_VERTICALLY ->
                new AlignVerticallyCommand(alignmemt);
            case ALIGN_RIGHT ->
                new AlignRightCommand(alignmemt);
            case ALIGN_TOP ->
                new AlignTopCommand(alignmemt);
            case ALIGN_HORIZONTALLY ->
                new AlignHorizontallyCommand(alignmemt);
            case ALIGN_BOTTOM ->
                new AlignBottomCommand(alignmemt);
            case ZOOM_TO_FIT ->
                new ZoomToFitCommand(zoom);
            case ZOOM_IN ->
                new ZoomInCommand(zoom);
            case ZOOM_OUT ->
                new ZoomOutCommand(zoom);
            case RELOAD_PLUGINS ->
                new ReloadPluginsCommand();
            case HELP ->
                new HelpCommand();
        };
    }

    public Command createAddBlockCommand(String type, double x, double y) {
        var workspace = registry.active();
        var session = workspace.session();
        return new AddBlockCommand(session, type, x, y);
    }

    public Command createAddConnectionCommand(PortRef from, PortRef to) {
        var workspace = registry.active();
        var session = workspace.session();
        return new AddConnectionCommand(session, from, to);
    }

    public Command createRemoveConnectionCommand(Connection connection) {
        var workspace = registry.active();
        var session = workspace.session();
        return new RemoveConnectionCommand(session, connection);
    }

    public Command createRemoveGroupCommand(GroupId group) {
        var workspace = registry.active();
        var session = workspace.session();
        return new RemoveGroupCommand(session, group);
    }

    public Command createZoomCommand(double newScale, Point2D pivotPoint) { // <----------------------
        var workspace = registry.active();
        var zoomService = workspace.zoom();
        return new ZoomCommand(zoomService, newScale, pivotPoint);
    }

    public Command createRectangleSelectCommand(Bounds rectOnWorkspace) { // <----------------------
        var workspace = registry.active();
        var selection = workspace.selection();
        return new RectangleSelectCommand(selection, rectOnWorkspace);
    }

    public Command createUpdateSelectionCommand(BlockId block, boolean isModifierDown) {
        var workspace = registry.active();
        var session = workspace.session();
        return new UpdateSelectionCommand(session, block, isModifierDown);
    }

    public Command createMoveBlocksCommand(Collection<BlockId> blocks, double dx, double dy) { 
        var workspace = registry.active();
        var session = workspace.session();
        return new MoveBlocksCommand(session, blocks, dx, dy);
    }

    public Command createResizeBlockCommand(BlockId block, double width, double height) {
        var workspace = registry.active();
        var session = workspace.session();
        return new ResizeBlockCommand(session, block, width, height);
    }

    public Command createUpdateParamValueCommand(BlockId block, String valueId, String value) {
        var workspace = registry.active();
        var session = workspace.session();
        return new UpdateParamValueCommand(session, block, valueId, value);
    }

    public Command createRenameBlockCommand(BlockId block, String label) {
        var workspace = registry.active();
        var session = workspace.session();
        return new RenameBlockCommand(session, block, label);
    }
}

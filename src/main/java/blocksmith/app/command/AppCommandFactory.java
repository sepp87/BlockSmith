package blocksmith.app.command;

import blocksmith.app.workspace.command.NewFileCommand;
import blocksmith.app.command.Command;
import blocksmith.app.outbound.GraphRepo;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.Connection;
import blocksmith.domain.connection.PortRef;
import blocksmith.domain.group.GroupId;
import blocksmith.app.block.command.DeselectAllBlocksCommand;
import blocksmith.app.block.command.CopyBlocksCommand;
import blocksmith.app.group.command.AddGroupCommand;
import blocksmith.app.block.command.PasteBlocksCommand;
import blocksmith.app.block.command.RemoveBlocksCommand;
import blocksmith.app.workspace.command.SaveFileCommand;
import blocksmith.app.block.command.SelectAllBlocksCommand;
import blocksmith.app.block.command.UpdateSelectionCommand;
import blocksmith.app.block.command.AddBlockCommand;
import blocksmith.app.connection.command.AddConnectionCommand;
import blocksmith.app.connection.command.RemoveConnectionCommand;
import blocksmith.app.group.command.RemoveGroupCommand;
import blocksmith.app.block.command.MoveBlocksCommand;
import blocksmith.app.block.command.RenameBlockCommand;
import blocksmith.app.block.command.ResizeBlockCommand;
import blocksmith.app.block.command.UpdateParamValueCommand;
import blocksmith.app.outbound.WorkspaceRegistry;
import blocksmith.app.workspace.WorkspaceLifecycle;
import java.util.Collection;
import java.util.Optional;

/**
 *
 * @author joostmeulenkamp
 */
public class AppCommandFactory {

    private final WorkspaceLifecycle workspaces;
    private final WorkspaceRegistry registry;
    private final GraphRepo graphRepo;

    public AppCommandFactory(WorkspaceLifecycle workspaces, WorkspaceRegistry registry, GraphRepo graphRepo) {
        this.workspaces = workspaces;
        this.registry = registry;
        this.graphRepo = graphRepo;
    }

    public Optional<Command> createCommand(Command.Id id) {
        var workspace = registry.active();
        var session = workspace.session();

        Command command = null;
        switch (id) {
            case NEW_FILE ->
                command = new NewFileCommand(workspaces);
            case SAVE_FILE ->
                command = new SaveFileCommand(session);
            case COPY_BLOCKS ->
                command = new CopyBlocksCommand(session);
            case PASTE_BLOCKS ->
                command = new PasteBlocksCommand(session, null, null);
            case REMOVE_BLOCKS ->
                command = new RemoveBlocksCommand(session);
            case SELECT_ALL_BLOCKS ->
                command = new SelectAllBlocksCommand(session);
            case DESELECT_ALL_BLOCKS ->
                command = new DeselectAllBlocksCommand(session);
            case ADD_GROUP ->
                command = new AddGroupCommand(session);
            case RELOAD_PLUGINS ->
                command = new ReloadBlockDefsCommand();
        };
        return Optional.ofNullable(command);
    }

    public Command createAddBlockCommand(String type, double x, double y) { // <----------------------
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

    public Command createUpdateSelectionCommand(BlockId block, boolean isModifierDown) {
        var workspace = registry.active();
        var session = workspace.session();
        return new UpdateSelectionCommand(session, block, isModifierDown);
    }

    public Command createMoveBlocksCommand(Collection<BlockId> blocks, double dx, double dy) { // <----------------------
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

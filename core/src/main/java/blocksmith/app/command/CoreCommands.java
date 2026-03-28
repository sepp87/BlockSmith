package blocksmith.app.command;

import blocksmith.app.block.command.CopyBlocksCommand;
import blocksmith.app.block.command.DeselectAllBlocksCommand;
import blocksmith.app.block.command.PasteBlocksCommand;
import blocksmith.app.block.command.RemoveBlocksCommand;
import blocksmith.app.block.command.SelectAllBlocksCommand;
import blocksmith.app.group.command.AddGroupCommand;
import blocksmith.app.outbound.WorkspaceRegistry;
import blocksmith.app.workspace.WorkspaceLifecycle;
import blocksmith.app.workspace.command.NewFileCommand;
import blocksmith.app.workspace.command.SaveFileCommand;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 *
 * @author joost
 */
public class CoreCommands {

    private final Map<Enum<?>, Supplier<Command>> commands;

    public CoreCommands(WorkspaceLifecycle lifecycle, WorkspaceRegistry workspaces) {
        var temp = new HashMap<Enum<?>, Supplier<Command>>();
        temp.put(Command.Id.NEW_FILE, () -> new NewFileCommand(lifecycle));
        temp.put(Command.Id.SAVE_FILE, () -> new SaveFileCommand(workspaces.active().session()));
        temp.put(Command.Id.COPY_BLOCKS, () -> new CopyBlocksCommand(workspaces.active().session()));
        temp.put(Command.Id.PASTE_BLOCKS, () -> new PasteBlocksCommand(workspaces.active().session(), null, null));
        temp.put(Command.Id.REMOVE_BLOCKS, () -> new RemoveBlocksCommand(workspaces.active().session()));
        temp.put(Command.Id.SELECT_ALL_BLOCKS, () -> new SelectAllBlocksCommand(workspaces.active().session()));
        temp.put(Command.Id.DESELECT_ALL_BLOCKS, () -> new DeselectAllBlocksCommand(workspaces.active().session()));
        temp.put(Command.Id.ADD_GROUP, () -> new AddGroupCommand(workspaces.active().session()));
        commands = Map.copyOf(temp);
    }

    public void registerTo(CommandRegistry registry) {
        registry.registerAll(commands);
    }
}

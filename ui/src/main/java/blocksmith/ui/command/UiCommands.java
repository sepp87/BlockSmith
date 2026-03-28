package blocksmith.ui.command;

import blocksmith.app.block.command.PasteBlocksCommand;
import blocksmith.app.command.Command;
import blocksmith.app.command.CommandRegistry;
import blocksmith.app.workspace.WorkspaceLifecycle;
import blocksmith.ui.align.command.AlignBottomCommand;
import blocksmith.ui.align.command.AlignHorizontallyCommand;
import blocksmith.ui.align.command.AlignLeftCommand;
import blocksmith.ui.align.command.AlignRightCommand;
import blocksmith.ui.align.command.AlignTopCommand;
import blocksmith.ui.align.command.AlignVerticallyCommand;
import blocksmith.ui.editor.navigation.command.ZoomToFitCommand;
import blocksmith.ui.workspace.WorkspaceFxRegistry;
import blocksmith.ui.workspace.command.OpenFileCommand;
import blocksmith.ui.workspace.command.SaveAsFileCommand;
import blocksmith.ui.workspace.command.SaveFileUiCommand;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 *
 * @author joost
 */
public class UiCommands {

    private final Map<Enum<?>, Supplier<Command>> commands;

    public UiCommands(WorkspaceLifecycle lifecycle, WorkspaceFxRegistry workspaces) {
        var temp = new HashMap<Enum<?>, Supplier<Command>>();
        temp.put(Command.Id.OPEN_FILE, () -> new OpenFileCommand(lifecycle));
        temp.put(Command.Id.SAVE_FILE, () -> new SaveFileUiCommand(workspaces.active().session())); // falls back to save as, if save fails
        temp.put(Command.Id.SAVE_AS_FILE, () -> new SaveAsFileCommand(workspaces.active().session()));
        temp.put(Command.Id.PASTE_BLOCKS, () -> new PasteBlocksCommand(workspaces.active().session(), null, null));
        temp.put(Command.Id.ALIGN_BOTTOM, () -> new AlignBottomCommand(workspaces.active().alignment()));
        temp.put(Command.Id.ALIGN_HORIZONTALLY, () -> new AlignHorizontallyCommand(workspaces.active().alignment()));
        temp.put(Command.Id.ALIGN_LEFT, () -> new AlignLeftCommand(workspaces.active().alignment()));
        temp.put(Command.Id.ALIGN_RIGHT, () -> new AlignRightCommand(workspaces.active().alignment()));
        temp.put(Command.Id.ALIGN_TOP, () -> new AlignTopCommand(workspaces.active().alignment()));
        temp.put(Command.Id.ALIGN_VERTICALLY, () -> new AlignVerticallyCommand(workspaces.active().alignment()));
        temp.put(Command.Id.ZOOM_TO_FIT, () -> new ZoomToFitCommand(workspaces.active().zoom()));
        temp.put(Command.Id.ZOOM_IN, () -> new ZoomToFitCommand(workspaces.active().zoom()));
        temp.put(Command.Id.ZOOM_OUT, () -> new ZoomToFitCommand(workspaces.active().zoom()));
        temp.put(Command.Id.RELOAD_BLOCK_DEFS, () -> new ZoomToFitCommand(workspaces.active().zoom()));
        temp.put(Command.Id.HELP, () -> new ZoomToFitCommand(workspaces.active().zoom()));
        commands = Map.copyOf(temp);
    }

    public void registerTo(CommandRegistry registry) {
        registry.registerAll(commands);
    }
}

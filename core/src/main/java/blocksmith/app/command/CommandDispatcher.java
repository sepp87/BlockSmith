package blocksmith.app.command;

import blocksmith.app.outbound.WorkspaceRegistry;
import blocksmith.app.command.WorkspaceCommand;
import blocksmith.app.command.Command;
import java.util.logging.Logger;

/**
 *
 * @author Joost
 */
public class CommandDispatcher {
    
    private static final Logger LOGGER = Logger.getLogger(CommandDispatcher.class.getName());

    private final WorkspaceRegistry workspaces;
    private final CommandRegistry commands;

    public CommandDispatcher(WorkspaceRegistry workspaces, CommandRegistry commands) {
        this.workspaces = workspaces;
        this.commands = commands;
    }

    public void execute(Enum<?> id) {
        commands.create(id).ifPresentOrElse(
                command -> execute(command),
                () -> LOGGER.severe("Command NOT found: " + id)
        );

    }

    public void execute(Command command) {

        if (command instanceof WorkspaceCommand) {
            var workspace = workspaces.active();
            workspace.commandBus().execute(command);

        } else {
            command.execute();
        }

    }

    public void undo() {
        var workspace = workspaces.active();
        workspace.session().graphEditor().undo();

    }

    public void redo() {
        var workspace = workspaces.active();
        workspace.session().graphEditor().redo();

    }

}

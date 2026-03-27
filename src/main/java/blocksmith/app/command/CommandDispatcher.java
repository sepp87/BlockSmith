package blocksmith.app.command;

import blocksmith.app.outbound.WorkspaceRegistry;
import blocksmith.app.command.WorkspaceCommand;
import blocksmith.ui.command.AppFxCommandFactory;
import blocksmith.app.command.Command;

/**
 *
 * @author Joost
 */
public class CommandDispatcher {

    private final WorkspaceRegistry workspaces;
    private final AppFxCommandFactory commandFactory;

    public CommandDispatcher(WorkspaceRegistry workspaces, AppFxCommandFactory commandFactory) {
        this.workspaces = workspaces;
        this.commandFactory = commandFactory;
    }

    public AppFxCommandFactory getCommandFactory() {
        return commandFactory;
    }

    public void executeCommand(Command.Id name) {
        Command command = commandFactory.createCommand(name);
        if (command != null) {
            executeCommand(command);
        } else {
            System.out.println("Command not found: " + name);
        }
    }

    public void executeCommand(Command command) {

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

package blocksmith.ui.command;

import blocksmith.ui.command.Command;
import blocksmith.ui.workspace.FxWorkspaceRegistry;


/**
 *
 * @author Joost
 */
public class CommandDispatcher {

    private final FxWorkspaceRegistry context;
    private final AppCommandFactory commandFactory;

    public CommandDispatcher(FxWorkspaceRegistry context, AppCommandFactory commandFactory) {
        this.context = context;
        this.commandFactory = commandFactory;
    }

    public AppCommandFactory getCommandFactory() {
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
        var workspace = context.active();
        command.execute();
    }

    public void undo() {
        var workspace = context.active();
        workspace.session().graphEditor().undo();

    }

    public void redo() {
        var workspace = context.active();
        workspace.session().graphEditor().redo();

    }




}


package btscore.command;

import btscore.command.Command;
import btscore.editor.EditorContext;


/**
 *
 * @author Joost
 */
public class CommandDispatcher {

    private final EditorContext context;
    private final CommandFactory commandFactory;

    public CommandDispatcher(EditorContext context, CommandFactory commandFactory) {
        this.context = context;
        this.commandFactory = commandFactory;
    }

    public CommandFactory getCommandFactory() {
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
        var workspace = context.activeWorkspace();
        command.execute();
    }

    public void undo() {
        var workspace = context.activeWorkspace();
        workspace.controller().getModel().graphEditor().undo();

    }

    public void redo() {
        var workspace = context.activeWorkspace();
        workspace.controller().getModel().graphEditor().redo();

    }




}


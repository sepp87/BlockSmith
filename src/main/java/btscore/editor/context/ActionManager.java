package btscore.editor.context;


/**
 *
 * @author Joost
 */
public class ActionManager {

    private final EditorContext context;
    private final CommandFactory commandFactory;

    public ActionManager(EditorContext context, CommandFactory commandFactory) {
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
        command.execute(workspace);
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

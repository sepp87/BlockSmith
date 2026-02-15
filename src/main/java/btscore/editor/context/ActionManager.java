package btscore.editor.context;

import blocksmith.ui.WorkspaceSession;

/**
 *
 * @author Joost
 */
public class ActionManager {

    private final EditorContext context;
    private final CommandFactory commandFactory;

    public ActionManager(EditorContext context, WorkspaceSession session, CommandFactory commandFactory) {
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
        var history = context.activeWorkspace().history();
        var state = context.activeWorkspace().state();
        boolean isSuccessful = command.execute(workspace);
        if (isSuccessful) {

            if (command instanceof UndoableCommand undoable) {
                history.undoStack().push(undoable);
                history.redoStack().clear();
            } else if (command instanceof ResetHistoryCommand) {
                resetHistory();
            }

            if (command instanceof MarkSavedCommand) {
                var marker = history.undoStack().size();
                state.setSavepoint(marker);
            }
            updateSavableState();
        }
    }

    public void undo() {
        var workspace = context.activeWorkspace();
        context.activeWorkspace().session().undo();
        var history = context.activeWorkspace().history();
        if (!history.undoStack().isEmpty()) {
            UndoableCommand command = history.undoStack().pop();
            command.undo();
            history.redoStack().push(command);
            updateSavableState();
        }
    }

    public void redo() {
        var workspace = context.activeWorkspace();
        context.activeWorkspace().session().redo();
        var history = context.activeWorkspace().history();
        if (!history.redoStack().isEmpty()) {
            UndoableCommand command = history.redoStack().pop();
            command.execute(workspace);
            history.undoStack().push(command);
            updateSavableState();
        }
    }

    private void updateSavableState() {
        var history = context.activeWorkspace().history();
        var state = context.activeWorkspace().state();
        var isSavable = history.undoStack().size() != state.getSavepoint();
        state.setSavable(isSavable);
    }

    public void resetHistory() {
        var history = context.activeWorkspace().history();
        history.undoStack().clear();
        history.redoStack().clear();
    }

    public boolean hasUndoableCommands() {
        var history = context.activeWorkspace().history();
        return !history.undoStack().isEmpty();
    }

    public boolean hasRedoableCommands() {
        var history = context.activeWorkspace().history();
        return !history.redoStack().isEmpty();
    }

}

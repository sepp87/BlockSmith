package btscore.editor.context;

import blocksmith.ui.BlockModelFactory;
import java.util.ArrayDeque;
import java.util.Deque;
import btscore.workspace.WorkspaceController;
import btscore.workspace.WorkspaceModel;

/**
 *
 * @author Joost
 */
public class ActionManager {

    private final BlockModelFactory blockModelFactory;
    private final WorkspaceModel workspaceModel;
    private final WorkspaceController workspaceController;

    private final CommandFactory commandFactory;

    private final Deque<UndoableCommand> undoStack = new ArrayDeque<>();
    private final Deque<UndoableCommand> redoStack = new ArrayDeque<>();

    private int savepoint = -1;

    public ActionManager(BlockModelFactory blockModelFactory, WorkspaceModel workspaceModel, WorkspaceController workspaceController) {
        this.blockModelFactory = blockModelFactory;
        this.workspaceModel = workspaceModel;
        this.workspaceController = workspaceController;
        this.commandFactory = new CommandFactory(workspaceModel, workspaceController);
    }
    
    public BlockModelFactory getBlockModelFactory() {
        return blockModelFactory;
    }

    public WorkspaceModel getWorkspaceModel() {
        return workspaceModel;
    }

    public WorkspaceController getWorkspaceController() {
        return workspaceController;
    }

    public void executeCommand(String id) {
        Command command = commandFactory.createCommand(id);
        if (command != null) {
            executeCommand(command);
        } else {
            System.out.println("Command not found: " + id);
        }
    }

    public void executeCommand(Command command) {
        boolean isSuccessful = command.execute();
        if (isSuccessful) {

            if (command instanceof UndoableCommand undoable) {
                undoStack.push(undoable);
                redoStack.clear();
            } else if (command instanceof ResetHistoryCommand) {
                resetHistory();
            }

            if (command instanceof MarkSavedCommand) {
                savepoint = undoStack.size();
            }
            updateSavableState();
        }
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            UndoableCommand command = undoStack.pop();
            command.undo();
            redoStack.push(command);
            updateSavableState();
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            UndoableCommand command = redoStack.pop();
            command.execute();
            undoStack.push(command);
            updateSavableState();
        }
    }

    private void updateSavableState() {
        boolean isSavable = undoStack.size() != savepoint;
        workspaceModel.savableProperty().set(isSavable);
    }

    public void resetHistory() {
        undoStack.clear();
        redoStack.clear();
    }

    public boolean hasUndoableCommands() {
        return !undoStack.isEmpty();
    }

    public boolean hasRedoableCommands() {
        return !redoStack.isEmpty();
    }
}

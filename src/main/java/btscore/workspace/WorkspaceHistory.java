package btscore.workspace;

import btscore.editor.context.UndoableCommand;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 *
 * @author joost
 */
public record WorkspaceHistory(
        Deque<UndoableCommand> undoStack,
        Deque<UndoableCommand> redoStack) {

    public static WorkspaceHistory create() {
        return new WorkspaceHistory(
                new ArrayDeque<>(),
                new ArrayDeque<>()
        );
    }
}

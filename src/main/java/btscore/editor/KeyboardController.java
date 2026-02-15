package btscore.editor;

import btscore.editor.context.EditorContext;
import static javafx.scene.input.KeyCode.A;
import static javafx.scene.input.KeyCode.C;
import static javafx.scene.input.KeyCode.DELETE;
import static javafx.scene.input.KeyCode.G;
import static javafx.scene.input.KeyCode.N;
import static javafx.scene.input.KeyCode.O;
import static javafx.scene.input.KeyCode.S;
import static javafx.scene.input.KeyCode.V;
import javafx.scene.input.KeyEvent;
import btscore.UiApp;
import btscore.utils.EventUtils;
import btscore.editor.context.Command;

/**
 *
 * @author joostmeulenkamp
 */
public class KeyboardController {

    private final EditorContext context;

    public KeyboardController(EditorContext context) {
        this.context = context;
    }

    public void handleShortcutTriggered(KeyEvent event) {
        var workspace = context.activeWorkspace();
        var actionManager = workspace.actionManager();
        var state = workspace.state();
//        var session = workspace.sesion();
//        var model = workspace.model();
//        ActionManager actionManager = UiApp.getCurrentContext().getActionManager();

        if (UiApp.LOG_METHOD_CALLS) {
            System.out.println("KeyboardController.handleShortcutTriggered()");
        }
        Command command = null;
        boolean isModifierDown = EventUtils.isModifierDown(event);
        var commandFactory = workspace.commandFactory();

        switch (event.getCode()) {
            case BACK_SPACE:
            case DELETE:
                command = commandFactory.createCommand(Command.Id.REMOVE_BLOCKS);

                break;
            case C:
                if (isModifierDown) {
                    command = commandFactory.createCommand(Command.Id.COPY_BLOCKS);
                }
                break;
            case V:
                if (isModifierDown) {
                    command = commandFactory.createCommand(Command.Id.PASTE_BLOCKS);
                }
                break;
            case G:
                if (isModifierDown) {
                    command = commandFactory.createCommand(Command.Id.ADD_GROUP);
                }
                break;
            case N:
                if (isModifierDown) {
                    command = commandFactory.createCommand(Command.Id.NEW_FILE);
                }
                break;
            case S:
                if (isModifierDown) {
                    if (event.isShiftDown()) {
                        command = commandFactory.createCommand(Command.Id.SAVE_AS_FILE);

                    } else if (state.isSavable()) {
                        command = commandFactory.createCommand(Command.Id.SAVE_FILE);
                    }
                }
                break;
            case O:
                if (isModifierDown) {
                    command = commandFactory.createCommand(Command.Id.OPEN_FILE);
                }
                break;
            case A:
                if (isModifierDown) {
                    command = commandFactory.createCommand(Command.Id.SELECT_ALL_BLOCKS);
                }
                break;
            case PLUS:
                if (isModifierDown) {
                    command = commandFactory.createCommand(Command.Id.ZOOM_IN);

                }
                break;
            case MINUS:
                if (isModifierDown) {
                    command = commandFactory.createCommand(Command.Id.ZOOM_OUT);
                }
                break;
            case SPACE:
                command = commandFactory.createCommand(Command.Id.ZOOM_TO_FIT);
                break;
            case Z:
                if (isModifierDown) {
                    actionManager.undo();
//                    session.undo();
                }
                break;
            case Y:
                if (isModifierDown) {
                    actionManager.redo();
//                    session.redo();
                }
                break;

        }
        if (command != null) {
            actionManager.executeCommand(command);
        }
    }
}

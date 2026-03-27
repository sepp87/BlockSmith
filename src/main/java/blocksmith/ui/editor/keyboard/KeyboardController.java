package blocksmith.ui.editor.keyboard;

import blocksmith.ui.workspace.WorkspaceFxRegistry;
import static javafx.scene.input.KeyCode.A;
import static javafx.scene.input.KeyCode.C;
import static javafx.scene.input.KeyCode.DELETE;
import static javafx.scene.input.KeyCode.G;
import static javafx.scene.input.KeyCode.N;
import static javafx.scene.input.KeyCode.O;
import static javafx.scene.input.KeyCode.S;
import static javafx.scene.input.KeyCode.V;
import javafx.scene.input.KeyEvent;
import blocksmith.ui.UiApp;
import blocksmith.app.command.CommandDispatcher;
import blocksmith.ui.utils.EventUtils;
import blocksmith.app.command.Command;
import blocksmith.ui.command.AppFxCommandFactory;

/**
 *
 * @author joostmeulenkamp
 */
public class KeyboardController {

    private final CommandDispatcher actionManager;
    private final AppFxCommandFactory commandFactory;

    public KeyboardController(CommandDispatcher actionManager, AppFxCommandFactory commandFactory) {
        this.actionManager = actionManager;
        this.commandFactory = commandFactory;
    }

    public void handleShortcutTriggered(KeyEvent event) {

        if (UiApp.LOG_METHOD_CALLS) {
            System.out.println("KeyboardController.handleShortcutTriggered()");
        }
        Command command = null;
        boolean isModifierDown = EventUtils.isModifierDown(event);

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

                    } else {
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

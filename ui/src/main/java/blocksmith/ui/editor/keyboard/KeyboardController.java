package blocksmith.ui.editor.keyboard;

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
import blocksmith.utils.OperatingSystem;
import java.util.logging.Logger;

/**
 *
 * @author joostmeulenkamp
 */
public class KeyboardController {

    private static final Logger LOGGER = Logger.getLogger(KeyboardController.class.getName());

    private final CommandDispatcher commandDispatcher;

    public KeyboardController(CommandDispatcher commandDispatcher) {
        this.commandDispatcher = commandDispatcher;
    }

    public void handleShortcutTriggered(KeyEvent event) {

        var isModifierDown = EventUtils.isModifierDown(event);

        var withModifier = isModifierDown ? ", with modifier key down" : "";
        LOGGER.fine("Key pressed: " + event.getCode() + withModifier);

        switch (event.getCode()) {

            case BACK_SPACE:
            case DELETE:
                commandDispatcher.execute(Command.Id.REMOVE_BLOCKS);

                break;
            case C:
                if (isModifierDown) {
                    commandDispatcher.execute(Command.Id.COPY_BLOCKS);
                }
                break;
            case V:
                if (isModifierDown) {
                    commandDispatcher.execute(Command.Id.PASTE_BLOCKS);
                }
                break;
            case G:
                if (isModifierDown) {
                    commandDispatcher.execute(Command.Id.ADD_GROUP);
                }
                break;
            case N:
                if (isModifierDown) {
                    commandDispatcher.execute(Command.Id.NEW_FILE);
                }
                break;
            case S:
                if (isModifierDown) {
                    if (event.isShiftDown()) {
                        commandDispatcher.execute(Command.Id.SAVE_AS_FILE);

                    } else {
                        commandDispatcher.execute(Command.Id.SAVE_FILE);
                    }
                }
                break;
            case O:
                if (isModifierDown) {
                    commandDispatcher.execute(Command.Id.OPEN_FILE);
                }
                break;
            case A:
                if (isModifierDown) {
                    commandDispatcher.execute(Command.Id.SELECT_ALL_BLOCKS);
                }
                break;
            case PLUS:
                if (isModifierDown) {
                    commandDispatcher.execute(Command.Id.ZOOM_IN);

                }
                break;
            case MINUS:
                if (isModifierDown) {
                    commandDispatcher.execute(Command.Id.ZOOM_OUT);
                }
                break;
            case SPACE:
                commandDispatcher.execute(Command.Id.ZOOM_TO_FIT);
                break;
            case Z:
                if (isModifierDown) {
                    commandDispatcher.undo();
                }
                break;
            case Y:
                if (isModifierDown) {
                    commandDispatcher.redo();
                }
                break;

        }
    }
}

package blocksmith.ui.help.command;

import blocksmith.ui.help.HelpDialog;
import blocksmith.app.command.AppCommand;
import blocksmith.ui.UiApp;
import blocksmith.ui.UserPrefsService;

/**
 *
 * @author joostmeulenkamp
 */
public class HelpCommand implements AppCommand {

    private final UserPrefsService userPrefsService;

    public HelpCommand(UserPrefsService userPrefsService) {
        this.userPrefsService = userPrefsService;
    }

    @Override
    public boolean execute() {
        HelpDialog.show(UiApp.getStage(), userPrefsService);
        return true;
    }

}

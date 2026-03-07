package blocksmith.ui.command.app;

import blocksmith.ui.HelpDialog;
import blocksmith.ui.command.AppCommand;

/**
 *
 * @author joostmeulenkamp
 */
public class HelpCommand implements AppCommand {

    @Override
    public boolean execute() {
        HelpDialog.show();
        return true;
    }

}

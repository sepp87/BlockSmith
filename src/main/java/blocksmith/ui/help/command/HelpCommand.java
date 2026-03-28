package blocksmith.ui.help.command;

import blocksmith.ui.help.HelpDialog;
import blocksmith.app.command.AppCommand;

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

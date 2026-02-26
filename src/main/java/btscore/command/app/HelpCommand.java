package btscore.command.app;

import btscore.HelpDialog;
import btscore.command.AppCommand;

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

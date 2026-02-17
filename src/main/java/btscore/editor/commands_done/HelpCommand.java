package btscore.editor.commands_done;

import btscore.HelpDialog;
import btscore.editor.context.Command;
import btscore.workspace.WorkspaceContext;

/**
 *
 * @author joostmeulenkamp
 */
public class HelpCommand implements Command {

    @Override
    public boolean execute(WorkspaceContext context) {
        HelpDialog.show();
        return true;
    }

}

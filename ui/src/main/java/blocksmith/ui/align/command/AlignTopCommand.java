package blocksmith.ui.align.command;

import blocksmith.ui.align.AlignmentPolicy;
import blocksmith.ui.align.AlignmentService;
import blocksmith.app.command.WorkspaceCommand;
import blocksmith.app.workspace.WorkspaceSession;

/**
 *
 * @author JoostMeulenkamp
 */
public class AlignTopCommand implements WorkspaceCommand {

private final AlignmentService align;

    public AlignTopCommand(AlignmentService align) {
        this.align = align;
    }

    @Override
    public boolean execute() {
        align.apply(AlignmentPolicy.Mode.TOP);
        return true;
    }

}
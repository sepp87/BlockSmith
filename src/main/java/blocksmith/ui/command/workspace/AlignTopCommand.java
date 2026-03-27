package blocksmith.ui.command.workspace;

import blocksmith.ui.AlignmentPolicy;
import blocksmith.ui.AlignmentService;
import blocksmith.app.workspace.WorkspaceCommand;
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
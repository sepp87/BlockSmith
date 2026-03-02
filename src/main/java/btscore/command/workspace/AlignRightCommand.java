package btscore.command.workspace;

import blocksmith.ui.AlignmentPolicy;
import btscore.command.WorkspaceCommand;
import btscore.workspace.WorkspaceSession;

/**
 *
 * @author JoostMeulenkamp
 */
public class AlignRightCommand implements WorkspaceCommand {

    private final WorkspaceSession session;

    public AlignRightCommand(WorkspaceSession session) {
        this.session = session;
    }

    @Override
    public boolean execute() {
        session.alignmentService().align(AlignmentPolicy.Mode.RIGHT);
        return true;
    }

}

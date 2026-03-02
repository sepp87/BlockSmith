package btscore.command.workspace;

import blocksmith.ui.AlignmentPolicy;
import btscore.command.WorkspaceCommand;
import btscore.workspace.WorkspaceSession;

/**
 *
 * @author JoostMeulenkamp
 */
public class AlignHorizontallyCommand implements WorkspaceCommand {

    private final WorkspaceSession session;

    public AlignHorizontallyCommand(WorkspaceSession session) {
        this.session = session;
    }

    @Override
    public boolean execute() {
        session.alignmentService().align(AlignmentPolicy.Mode.HORIZONTALLY);

        return true;
    }

}

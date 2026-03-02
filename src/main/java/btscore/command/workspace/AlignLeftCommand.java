package btscore.command.workspace;

import blocksmith.ui.AlignmentPolicy;
import btscore.command.WorkspaceCommand;
import btscore.workspace.WorkspaceSession;

/**
 *
 * @author JoostMeulenkamp
 */
public class AlignLeftCommand implements WorkspaceCommand {

    private final WorkspaceSession session;

    public AlignLeftCommand(WorkspaceSession session) {
        this.session = session;
    }

    @Override
    public boolean execute() {
        session.alignmentService().align(AlignmentPolicy.Mode.LEFT);

        return true;
    }

}

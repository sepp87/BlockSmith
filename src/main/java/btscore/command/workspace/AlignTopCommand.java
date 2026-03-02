package btscore.command.workspace;

import blocksmith.ui.AlignmentPolicy;
import btscore.command.WorkspaceCommand;
import btscore.workspace.WorkspaceSession;

/**
 *
 * @author JoostMeulenkamp
 */
public class AlignTopCommand implements WorkspaceCommand {

    private final WorkspaceSession session;

    public AlignTopCommand(WorkspaceSession session) {
        this.session = session;
    }

    @Override
    public boolean execute() {

        session.alignmentService().align(AlignmentPolicy.Mode.TOP);

        return true;
    }

}

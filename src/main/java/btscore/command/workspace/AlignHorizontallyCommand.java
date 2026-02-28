package btscore.command.workspace;

import blocksmith.ui.AlignmentPolicy;
import btscore.command.WorkspaceCommand;
import btscore.workspace.WorkspaceModel;

/**
 *
 * @author JoostMeulenkamp
 */
public class AlignHorizontallyCommand implements WorkspaceCommand {

    private final WorkspaceModel session;

    public AlignHorizontallyCommand(WorkspaceModel session) {
        this.session = session;
    }

    @Override
    public boolean execute() {
        session.alignmentService().align(AlignmentPolicy.Mode.HORIZONTALLY);

        return true;
    }

}

package btscore.command.workspace;

import blocksmith.ui.AlignmentPolicy;
import btscore.command.WorkspaceCommand;
import btscore.workspace.WorkspaceModel;

/**
 *
 * @author JoostMeulenkamp
 */
public class AlignRightCommand implements WorkspaceCommand {

    private final WorkspaceModel session;

    public AlignRightCommand(WorkspaceModel session) {
        this.session = session;
    }

    @Override
    public boolean execute() {
        session.alignmentService().align(AlignmentPolicy.Mode.RIGHT);
        return true;
    }

}

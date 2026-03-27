package blocksmith.ui.command.workspace;

import blocksmith.ui.AlignmentPolicy;
import blocksmith.ui.AlignmentService;
import blocksmith.app.workspace.WorkspaceCommand;

/**
 *
 * @author JoostMeulenkamp
 */
public class AlignRightCommand implements WorkspaceCommand {

private final AlignmentService align;

    public AlignRightCommand(AlignmentService align) {
        this.align = align;
    }

    @Override
    public boolean execute() {
        align.apply(AlignmentPolicy.Mode.RIGHT);
        return true;
    }

}
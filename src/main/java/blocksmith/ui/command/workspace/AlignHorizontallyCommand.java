package blocksmith.ui.command.workspace;

import blocksmith.ui.AlignmentPolicy;
import blocksmith.ui.AlignmentService;
import blocksmith.app.workspace.WorkspaceCommand;

/**
 *
 * @author JoostMeulenkamp
 */
public class AlignHorizontallyCommand implements WorkspaceCommand {

    private final AlignmentService align;

    public AlignHorizontallyCommand(AlignmentService align) {
        this.align = align;
    }

    @Override
    public boolean execute() {
        align.apply(AlignmentPolicy.Mode.HORIZONTALLY);
        return true;
    }

}

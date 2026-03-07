package blocksmith.ui.command.workspace;

import blocksmith.ui.AlignmentPolicy;
import blocksmith.ui.AlignmentService;
import blocksmith.ui.command.WorkspaceCommand;

/**
 *
 * @author JoostMeulenkamp
 */
public class AlignBottomCommand implements WorkspaceCommand {

    private final AlignmentService align;

    public AlignBottomCommand(AlignmentService align) {
        this.align = align;
    }

    @Override
    public boolean execute() {
        align.apply(AlignmentPolicy.Mode.BOTTOM);
        return true;
    }

}

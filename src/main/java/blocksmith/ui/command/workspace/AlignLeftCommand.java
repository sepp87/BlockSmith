package blocksmith.ui.command.workspace;

import blocksmith.ui.AlignmentPolicy;
import blocksmith.ui.AlignmentService;
import blocksmith.ui.command.WorkspaceCommand;

/**
 *
 * @author JoostMeulenkamp
 */
public class AlignLeftCommand implements WorkspaceCommand {

    private final AlignmentService align;

    public AlignLeftCommand(AlignmentService align) {
        this.align = align;
    }

    @Override
    public boolean execute() {
        align.apply(AlignmentPolicy.Mode.LEFT);
        return true;
    }

}

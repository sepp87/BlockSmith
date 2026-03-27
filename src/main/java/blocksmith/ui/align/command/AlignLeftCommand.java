package blocksmith.ui.align.command;

import blocksmith.ui.align.AlignmentPolicy;
import blocksmith.ui.align.AlignmentService;
import blocksmith.app.command.WorkspaceCommand;

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

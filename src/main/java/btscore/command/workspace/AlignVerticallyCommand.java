package btscore.command.workspace;

import blocksmith.ui.AlignmentPolicy;
import java.util.Map;
import java.util.TreeMap;
import btscore.command.WorkspaceCommand;
import btscore.workspace.WorkspaceSession;

/**
 *
 * @author JoostMeulenkamp
 */
public class AlignVerticallyCommand implements WorkspaceCommand {

    private final WorkspaceSession session;
    private final Map<String, Double> previousLocations = new TreeMap<>();

    public AlignVerticallyCommand(WorkspaceSession session) {
        this.session = session;
    }

    @Override
    public boolean execute() {
        session.alignmentService().align(AlignmentPolicy.Mode.VERTICALLY);
        return true;
    }

}

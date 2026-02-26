package btscore.command.workspace;

import blocksmith.ui.AlignmentPolicy;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import btscore.graph.block.BlockController;
import btscore.workspace.WorkspaceController;
import btscore.command.WorkspaceCommand;
import btscore.workspace.WorkspaceModel;

/**
 *
 * @author JoostMeulenkamp
 */
public class AlignHorizontallyCommand implements WorkspaceCommand {

    private final WorkspaceModel session;
    private final Collection<BlockController> blocks;
    private final Map<String, Double> previousLocations = new TreeMap<>();

    public AlignHorizontallyCommand(WorkspaceController workspace, WorkspaceModel session) {
        this.session = session;
        this.blocks = workspace.getSelectedBlockControllers();
    }

    @Override
    public boolean execute() {
        var views = blocks.stream().map(b -> b.getView()).toList();
        var align = new AlignmentPolicy();
        var requests = align.apply(views, AlignmentPolicy.Mode.HORIZONTALLY);
        session.graphEditor().moveBlocks(requests);

        return true;
    }

}

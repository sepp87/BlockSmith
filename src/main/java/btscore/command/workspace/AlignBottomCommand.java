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
public class AlignBottomCommand implements WorkspaceCommand {

    private final WorkspaceModel workspaceModel;
    private final Collection<BlockController> blocks;
    private final Map<String, Double> previousLocations = new TreeMap<>();

    public AlignBottomCommand(WorkspaceController workspace, WorkspaceModel workspaceModel) {
        this.workspaceModel = workspaceModel;
        this.blocks = workspace.getSelectedBlockControllers();
        System.out.println(blocks.size() + " number of blocks");
    }

    @Override
    public boolean execute() {
        var views = blocks.stream().map(b -> b.getView()).toList();
        var align = new AlignmentPolicy();
        var requests = align.apply(views, AlignmentPolicy.Mode.BOTTOM);
        workspaceModel.graphEditor().moveBlocks(requests);

        return true;
    }

}

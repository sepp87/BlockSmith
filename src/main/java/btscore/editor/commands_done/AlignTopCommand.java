package btscore.editor.commands_done;

import blocksmith.ui.AlignmentPolicy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javafx.geometry.Bounds;
import btscore.graph.block.BlockController;
import btscore.graph.block.BlockModel;
import btscore.graph.block.BlockView;
import btscore.workspace.WorkspaceController;
import btscore.editor.context.UndoableCommand;
import btscore.workspace.WorkspaceContext;
import btscore.workspace.WorkspaceModel;

/**
 *
 * @author JoostMeulenkamp
 */
public class AlignTopCommand implements UndoableCommand {

    private final WorkspaceModel session;
    private final Collection<BlockController> blocks;
    private final Map<String, Double> previousLocations = new TreeMap<>();

    public AlignTopCommand(WorkspaceController workspace, WorkspaceModel session) {
        this.session = session;
        this.blocks = workspace.getSelectedBlockControllers();
    }

    @Override
    public boolean execute(WorkspaceContext context) {

        var views = blocks.stream().map(b -> b.getView()).toList();
        var align = new AlignmentPolicy();
        var requests = align.apply(views, AlignmentPolicy.Mode.TOP);
        session.graphEditor().moveBlocks(requests);

        // OLD STUFF
        List<BlockView> blockViews = new ArrayList<>();
        for (BlockController blockController : blocks) {
            blockViews.add(blockController.getView());
        }
        Bounds bBox = BlockView.getBoundingBoxOfBlocks(blockViews);
        for (BlockController blockController : blocks) {
            BlockModel blockModel = blockController.getModel();
            previousLocations.put(blockModel.getId(), blockModel.layoutYProperty().get());
            blockModel.layoutYProperty().set(bBox.getMinY());
        }
        return true;
    }

    @Override
    public void undo() {
        for (BlockController blockController : blocks) {
            BlockModel blockModel = blockController.getModel();
            blockModel.layoutYProperty().set(previousLocations.get(blockModel.getId()));
        }
    }
}

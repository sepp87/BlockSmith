package btscore.command.workspace;

import blocksmith.app.logging.GraphLogFmt;
import blocksmith.domain.block.BlockPosition;
import blocksmith.domain.block.BlockId;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import javafx.geometry.Point2D;
import btscore.graph.block.BlockController;
import btscore.graph.block.BlockModel;
import btscore.command.WorkspaceCommand;
import btscore.workspace.WorkspaceSession;
import java.util.ArrayList;
import javax.lang.model.element.UnknownElementException;

/**
 *
 * @author JoostMeulenkamp
 */
public class MoveBlocksCommand implements WorkspaceCommand {

    private final WorkspaceSession workspace;
    private final Collection<BlockId> ids;
    private final Point2D delta;

    public MoveBlocksCommand(WorkspaceSession workspace, Collection<BlockId> blocks, Point2D delta) {
        this.workspace = workspace;
        this.ids = blocks;
        this.delta = delta;
    }

    @Override
    public boolean execute() {
        var requests = new ArrayList<BlockPosition>(ids.size());

        for (var id : ids) {
            var block = workspace.graphSnapshot().block(id)
                    .orElseThrow(() -> new IllegalStateException("FxBlock missing in graph, id: " + GraphLogFmt.block(id)));
            var request = new BlockPosition(
                    block.id(),
                    block.layout().x() + delta.getX(),
                    block.layout().y() + delta.getY()
            );
            requests.add(request);
        }
        workspace.graphEditor().moveBlocks(requests);
        return true;
    }
}

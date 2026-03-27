package blocksmith.app.block.command;

import blocksmith.app.logging.GraphLogFmt;
import blocksmith.domain.block.BlockPosition;
import blocksmith.domain.block.BlockId;
import java.util.Collection;
import blocksmith.app.workspace.WorkspaceCommand;
import blocksmith.app.workspace.WorkspaceSession;
import java.util.ArrayList;

/**
 *
 * @author JoostMeulenkamp
 */
public class MoveBlocksCommand implements WorkspaceCommand {

    private final WorkspaceSession workspace;
    private final Collection<BlockId> ids;
    private final double dx;
    private final double dy;

    public MoveBlocksCommand(WorkspaceSession workspace, Collection<BlockId> blocks, double dx, double dy) {
        this.workspace = workspace;
        this.ids = blocks;
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public boolean execute() {
        var requests = new ArrayList<BlockPosition>(ids.size());

        for (var id : ids) {
            var block = workspace.graphSnapshot().block(id)
                    .orElseThrow(() -> new IllegalStateException("FxBlock missing in graph, id: " + GraphLogFmt.block(id)));
            var request = new BlockPosition(
                    block.id(),
                    block.layout().x() + dx,
                    block.layout().y() + dy
            );
            requests.add(request);
        }
        workspace.graphEditor().moveBlocks(requests);
        return true;
    }
}

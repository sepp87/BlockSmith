package blocksmith.ui.view;

import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.Connection;
import blocksmith.domain.group.GroupId;
import blocksmith.app.workspace.WorkspaceCommandBus;
import blocksmith.ui.graph.block.BlockController;
import blocksmith.ui.graph.block.BlockView;
import blocksmith.ui.graph.connection.ConnectionController;
import blocksmith.ui.graph.connection.ConnectionView;
import blocksmith.ui.graph.group.BlockGroupController;
import blocksmith.ui.graph.group.BlockGroupView;
import blocksmith.ui.projection.GraphProjection;
import blocksmith.ui.workspace.WorkspaceController;
import blocksmith.app.workspace.WorkspaceSession;
import blocksmith.ui.workspace.WorkspaceState;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author joost
 */
public class GraphFxMapper {

    private final WorkspaceSession session;
    private final WorkspaceCommandBus commands;
    private final GraphProjection projection;
    private final WorkspaceController renderer;
    private final WorkspaceState state;

    public GraphFxMapper(
            WorkspaceSession session,
            WorkspaceCommandBus commands,
            GraphProjection projection,
            WorkspaceController renderer,
            WorkspaceState state
    ) {
        this.session = session;
        this.commands = commands;
        this.projection = projection;
        this.renderer = renderer;
        this.state = state;
    }

    public Map<BlockId, BlockController> blocksToFx(Collection<BlockId> ids) {
        var result = new HashMap<BlockId, BlockController>();

        for (var id : ids) {
            var model = projection.block(id);
            var view = new BlockView();
            var controller = new BlockController(commands, session, projection, renderer, model, view);
            result.put(id, controller);
        }

        return result;
    }

    public Map<Connection, ConnectionController> connectionsToFx(Collection<Connection> ids) {
        var result = new HashMap<Connection, ConnectionController>();

        for (var id : ids) {
            var model = projection.connection(id);
            var view = new ConnectionView();
            var controller = new ConnectionController(commands, session, renderer, model, view);
            result.put(id, controller);
        }

        return result;
    }

    public Map<GroupId, BlockGroupController> groupsToFx(Collection<GroupId> ids, Map<BlockId, BlockController> blockIndex) {
        var result = new HashMap<GroupId, BlockGroupController>();

        for (var id : ids) {
            var model = projection.group(id);
            var view = new BlockGroupView();
            var controller = new BlockGroupController(commands, session, state, renderer, model, view);
            var blocks = model.getBlocks().stream().map(b -> blockIndex.get(BlockId.from(b.getId()))).toList();
            controller.setBlocks(blocks);
            result.put(id, controller);
        }

        return result;
    }
}

package blocksmith.ui.workspace;

import blocksmith.ui.projection.GraphProjection;
import blocksmith.ui.projection.GraphProjectionAssembler;
import blocksmith.app.outbound.WorkspaceFactory;
import blocksmith.ui.AlignmentService;
import blocksmith.ui.BlockModelFactory;
import blocksmith.app.workspace.WorkspaceSessionFactory;
import blocksmith.ui.command.AppCommandFactory;
import blocksmith.ui.command.WorkspaceCommandBus;
import blocksmith.ui.command.WorkspaceCommandFactory;
import java.nio.file.Path;

/**
 *
 * @author joost
 */
public class FxWorkspaceFactory implements WorkspaceFactory{

    private final WorkspaceSessionFactory sessionFactory;
    private final BlockModelFactory blockFactory;

    public FxWorkspaceFactory(
            WorkspaceSessionFactory sessionFactory,
            BlockModelFactory blockFactory
    ) {
        this.sessionFactory = sessionFactory;
        this.blockFactory = blockFactory;
    }

    @Override
    public FxWorkspaceHandle newDocument() {
        var session = sessionFactory.newDocument();
        return build(session);
    }

    @Override
    public FxWorkspaceHandle openDocument(Path path) throws Exception {
        var session = sessionFactory.openDocument(path);
        return build(session);
    }

    private FxWorkspaceHandle build(WorkspaceSession session) {
        var view = new WorkspaceView();
        var mapper = new GraphProjectionAssembler(blockFactory);
        var projection = new GraphProjection(mapper, session.graphSnapshot());
        var commandFactory = new WorkspaceCommandFactory(session);
        var commandBus = new WorkspaceCommandBus(commandFactory, session);
        var state = new WorkspaceState();
        var renderer = new WorkspaceController(commandBus, session, state, view, projection);
        var zoom = new ZoomService(session, view, projection);
        var alignment = new AlignmentService(session.selectionModel(), projection, session.graphEditor());
        var selection = new SelectionService(session.selectionModel(), projection);
        var context = new FxWorkspaceHandle(session, view, projection, commandBus, state, renderer, zoom, alignment, selection);

        // listeners
        session.addGraphListener(projection::updateFrom);
        projection.addProjectionListener(renderer::updateFrom);

        return context;
    }

}

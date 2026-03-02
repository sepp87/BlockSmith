package btscore.workspace;

import blocksmith.app.GraphDocument;
import blocksmith.ui.BlockModelFactory;
import blocksmith.ui.workspace.WorkspaceSessionFactory;
import btscore.command.CommandDispatcher;
import btscore.command.CommandFactory;
import btscore.command.WorkspaceCommandBus;
import java.nio.file.Path;
import java.util.Objects;

/**
 *
 * @author joost
 */
public class FxWorkspaceFactory {

    private final WorkspaceSessionFactory sessionFactory;
    private final CommandFactory commandFactory;
    private final BlockModelFactory blockFactory;

    public FxWorkspaceFactory(
            WorkspaceSessionFactory sessionFactory,
            CommandFactory commandFactory,
            BlockModelFactory blockFactory
    ) {
        this.sessionFactory = sessionFactory;
        this.commandFactory = commandFactory;
        this.blockFactory = blockFactory;
    }

    public WorkspaceContext newDocument() {
        var session = sessionFactory.newDocument();
        return build(session);
    }

    public WorkspaceContext openDocument(Path path) throws Exception {
        var session = sessionFactory.openDocument(path);
        return build(session);
    }

    private WorkspaceContext build(WorkspaceSession session) {
        var mapper = new GraphProjectionMapper(blockFactory);
        var projection = new GraphProjection(mapper, session.graphSnapshot());
        var commandBus = new WorkspaceCommandBus(commandFactory, session);
        var state = new WorkspaceState();
        var view = new WorkspaceView();
        var context = new WorkspaceContext(state);
        var renderer = new WorkspaceController(commandBus, session, state, view, projection);
        context.attachController(renderer);

        // listeners
        session.addGraphListener(projection::updateFrom);
        projection.addProjectionListener(renderer::updateFrom);

        return context;
    }

}

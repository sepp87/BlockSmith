package blocksmith.ui.workspace;

import blocksmith.ui.editor.selection.SelectionService;
import blocksmith.ui.editor.navigation.ZoomService;
import blocksmith.app.workspace.WorkspaceSession;
import blocksmith.ui.projection.GraphProjection;
import blocksmith.ui.projection.GraphProjectionAssembler;
import blocksmith.app.outbound.WorkspaceFactory;
import blocksmith.ui.align.AlignmentService;
import blocksmith.ui.graph.block.BlockModelFactory;
import blocksmith.app.workspace.WorkspaceSessionFactory;
import blocksmith.app.workspace.WorkspaceCommandBus;
import java.nio.file.Path;
import javafx.application.Platform;

/**
 *
 * @author joost
 */
public class WorkspaceFxFactory implements WorkspaceFactory {

    private final WorkspaceSessionFactory sessionFactory;
    private final BlockModelFactory blockFactory;

    public WorkspaceFxFactory(
            WorkspaceSessionFactory sessionFactory,
            BlockModelFactory blockFactory) {

        this.sessionFactory = sessionFactory;
        this.blockFactory = blockFactory;
    }

    @Override
    public WorkspaceFxHandle newDocument() {
        var session = sessionFactory.newDocument();
        return build(session);
    }

    @Override
    public WorkspaceFxHandle openDocument(Path path) throws Exception {
        var session = sessionFactory.openDocument(path);
        return build(session);
    }

    private WorkspaceFxHandle build(WorkspaceSession session) {
        var view = new WorkspaceView();
        var runtime = session.runtimeState();
        var mapper = new GraphProjectionAssembler(blockFactory, runtime);
        var projection = new GraphProjection(mapper, session.graphSnapshot());
        var commandBus = new WorkspaceCommandBus();
        var state = new WorkspaceState();
        var renderer = new WorkspaceController(commandBus, session, state, view, projection);
        var zoom = new ZoomService(session, session.selection(), view, projection);
        var alignment = new AlignmentService(session.selection(), projection, session.graphEditor());
        var selection = new SelectionService(session.selection(), projection);
        var viewport = new ViewportFxModel(session.viewport());
        var context = new WorkspaceFxHandle(session, view, projection, commandBus, state, renderer, zoom, alignment, selection, viewport);

        // bindings
        bindWorkspaceToViewport(view, viewport);

        // listeners
        session.addViewportListener(viewport::onViewportChanged);

        session.addGraphListener(projection::updateFromGraphState);
        projection.addProjectionListener(renderer::updateFrom);

        runtime.setOnBlockUpdated(blockId -> Platform.runLater(() -> {
            projection.block(blockId).updateFrom(runtime);
        }));

        return context;
    }

    private static void bindWorkspaceToViewport(WorkspaceView view, ViewportFxModel viewport) {
        view.scaleXProperty().bind(viewport.zoomFactorProperty());
        view.scaleYProperty().bind(viewport.zoomFactorProperty());
        view.translateXProperty().bind(viewport.translateXProperty());
        view.translateYProperty().bind(viewport.translateYProperty());
    }

}

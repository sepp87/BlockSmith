package blocksmith.ui.workspace;

import blocksmith.ui.editor.selection.SelectionService;
import blocksmith.ui.editor.navigation.ZoomService;
import blocksmith.app.workspace.WorkspaceSession;
import blocksmith.ui.projection.GraphProjection;
import blocksmith.app.inbound.GraphMutationAndHistory;
import blocksmith.app.outbound.WorkspaceHandle;
import blocksmith.ui.align.AlignmentService;
import blocksmith.app.workspace.WorkspaceCommandBus;
import java.util.UUID;

/**
 *
 * @author Joost
 */
public class WorkspaceFxHandle implements WorkspaceHandle {

    private final String id;
    private final WorkspaceSession session;
    private final WorkspaceView view;
    private final GraphProjection projection;
    private final WorkspaceCommandBus commandBus;
    private final WorkspaceState state;
    private final WorkspaceController renderer;
    private final ZoomService zoom;
    private final AlignmentService alignment;
    private final SelectionService selection;
    private final ViewportFxModel viewport;

    public WorkspaceFxHandle(
            WorkspaceSession session,
            WorkspaceView view,
            GraphProjection projection,
            WorkspaceCommandBus commandBus,
            WorkspaceState state,
            WorkspaceController renderer,
            ZoomService zoom,
            AlignmentService alignment,
            SelectionService selection,
            ViewportFxModel viewport
    ) {
        this.id = UUID.randomUUID().toString();
        this.session = session;
        this.view = view;
        this.projection = projection;
        this.commandBus = commandBus;
        this.state = state;
        this.renderer = renderer;
        this.zoom = zoom;
        this.alignment = alignment;
        this.selection = selection;
        this.viewport = viewport;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public WorkspaceSession session() {
        return session;
    }
    
    @Override
    public WorkspaceCommandBus commandBus() {
        return commandBus;
    }

    public WorkspaceState state() {
        return state;
    }

//    public WorkspaceController controller() {
//        return renderer;
//    }

    public WorkspaceView view() {
        return view;
    }

    public GraphMutationAndHistory graphEditor() {
        return session.graphEditor();
    }

    public ZoomService zoom() {
        return zoom;
    }

    public AlignmentService alignment() {
        return alignment;
    }
    
    public SelectionService selection() {
        return selection;
    }
    
    public ViewportFxModel viewport() {
        return viewport;
    }
}

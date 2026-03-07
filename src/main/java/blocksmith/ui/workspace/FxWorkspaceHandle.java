package blocksmith.ui.workspace;

import blocksmith.ui.projection.GraphProjection;
import blocksmith.app.inbound.GraphMutationAndHistory;
import blocksmith.app.outbound.WorkspaceHandle;
import blocksmith.ui.AlignmentService;
import blocksmith.ui.command.WorkspaceCommandBus;
import java.util.UUID;

/**
 *
 * @author Joost
 */
public class FxWorkspaceHandle implements WorkspaceHandle {

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

    public FxWorkspaceHandle(
            WorkspaceSession session,
            WorkspaceView view,
            GraphProjection projection,
            WorkspaceCommandBus commandBus,
            WorkspaceState state,
            WorkspaceController renderer,
            ZoomService zoom,
            AlignmentService alignment,
            SelectionService selection
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
}

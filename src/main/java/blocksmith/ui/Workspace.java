package blocksmith.ui;

import btscore.workspace.WorkspaceController;
import btscore.workspace.WorkspaceModel;
import btscore.workspace.WorkspaceView;
import java.util.UUID;

/**
 *
 * @author joost
 */
public record Workspace (
        UUID id,
        WorkspaceModel model, 
        WorkspaceController controller,
        WorkspaceView view,
        WorkspaceSession sesion
        ) {

    public static Workspace create(WorkspaceModel model, WorkspaceController controller, WorkspaceView view, WorkspaceSession session) {
        return new Workspace(UUID.randomUUID(), model, controller, view, session);
    }
    
}

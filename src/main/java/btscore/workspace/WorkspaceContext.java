package btscore.workspace;

import blocksmith.ui.WorkspaceSession;
import btscore.editor.context.ActionManager;
import btscore.editor.context.CommandFactory;
import btscore.workspace.WorkspaceController;
import btscore.workspace.WorkspaceHistory;
import btscore.workspace.WorkspaceModel;
import btscore.workspace.WorkspaceView;
import java.util.UUID;

/**
 *
 * @author Joost
 */
public record WorkspaceContext(
        String id,
        WorkspaceController controller,
        WorkspaceSession session,
        WorkspaceState state,
        WorkspaceHistory history,
        ActionManager actionManager,
        CommandFactory commandFactory) {

    public static WorkspaceContext create(
            WorkspaceController controller,
            WorkspaceSession session,
            WorkspaceState state,
            WorkspaceHistory history,
            ActionManager actionManager,
            CommandFactory commandFactory) {

        return new WorkspaceContext(
                UUID.randomUUID().toString(),
                controller,
                session,
                state,
                history,
                actionManager,
                commandFactory);
    }

    public static WorkspaceContext createEmpty() {
        return new WorkspaceContext(
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public WorkspaceModel model() {
        return controller.getModel();
    }

    public WorkspaceView view() {
        return controller.getView();
    }

}

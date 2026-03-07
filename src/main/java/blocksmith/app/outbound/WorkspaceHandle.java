package blocksmith.app.outbound;

import blocksmith.ui.command.WorkspaceCommandBus;
import blocksmith.ui.workspace.WorkspaceSession;

/**
 *
 * @author joostmeulenkamp
 */
public interface WorkspaceHandle {

    String id();
    WorkspaceSession session();
    WorkspaceCommandBus commandBus();
}

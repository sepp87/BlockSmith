package blocksmith.app.outbound;

import blocksmith.app.workspace.WorkspaceCommandBus;
import blocksmith.app.workspace.WorkspaceSession;

/**
 *
 * @author joostmeulenkamp
 */
public interface WorkspaceHandle {

    String id();
    WorkspaceSession session();
    WorkspaceCommandBus commandBus();
}

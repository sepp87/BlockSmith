package blocksmith.app.workspace;

import blocksmith.app.workspace.WorkspaceSession;
import java.util.logging.Logger;

/**
 *
 * @author joost
 */
public class WorkspaceCommandBus {

    private final static Logger LOGGER = Logger.getLogger(WorkspaceCommandBus.class.getName());

    private final WorkspaceCommandFactory factory;
    private final WorkspaceSession session;

    public WorkspaceCommandBus(WorkspaceCommandFactory factory, WorkspaceSession session) {
        this.factory = factory;
        this.session = session;
    }

    public void execute(Command command) {
        if (command instanceof WorkspaceCommand) {
            command.execute();
        } else {
            LOGGER.severe("Command does NOT have workspace scope");
        }
    }

    public WorkspaceCommandFactory factory() {
        return factory;
    }

}

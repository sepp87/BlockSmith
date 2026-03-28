package blocksmith.app.workspace;

import blocksmith.app.command.WorkspaceCommand;
import blocksmith.app.command.Command;
import java.util.logging.Logger;

/**
 *
 * @author joost
 */
public class WorkspaceCommandBus {

    private final static Logger LOGGER = Logger.getLogger(WorkspaceCommandBus.class.getName());

    public WorkspaceCommandBus() {

    }

    public void execute(Command command) {
        if (command instanceof WorkspaceCommand) {
            command.execute();
        } else {
            LOGGER.severe("Command does NOT have workspace scope");
        }
    }


}

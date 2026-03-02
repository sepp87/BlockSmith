package btscore.command;

import btscore.workspace.WorkspaceSession;
import java.util.logging.Logger;

/**
 *
 * @author joost
 */
public class WorkspaceCommandBus {

    private final static Logger LOGGER = Logger.getLogger(WorkspaceCommandBus.class.getName());

    private final CommandFactory factory;
    private final WorkspaceSession session;

    public WorkspaceCommandBus(CommandFactory factory, WorkspaceSession session) {
        this.factory = factory;
        this.session = session;
    }

    public void execute(WorkspaceCommand.Id commandId) {
        var command = factory.createCommand(commandId);
        if (command instanceof WorkspaceCommand workspaceCommand) {
            execute(workspaceCommand);
        } else {
            LOGGER.severe("Command does NOT have workspace scope");
        }
    }

    public void execute(Command command) {
        if (command instanceof WorkspaceCommand) {
            command.execute();
        } else {
            LOGGER.severe("Command does NOT have workspace scope");
        }
    }

    public CommandFactory factory() {
        return factory;
    }

}

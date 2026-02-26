package btscore.command;

import btscore.workspace.WorkspaceModel;
import java.util.logging.Logger;

/**
 *
 * @author joost
 */
public class WorkspaceCommandBus {

    private final static Logger LOGGER = Logger.getLogger(WorkspaceCommandBus.class.getName());
    
    private final WorkspaceModel workspace;
    private final CommandFactory factory;
    
    public WorkspaceCommandBus(WorkspaceModel workspace, CommandFactory factory){
        this.workspace = workspace;
        this.factory = factory;
    }
    
    public void execute(WorkspaceCommand.Id commandId) {
        var command = factory.createCommand(commandId);
        if(command instanceof WorkspaceCommand workspaceCommand) {
            execute(workspaceCommand);
        } else {
            LOGGER.info("Command does NOT have workspace scope");
        }
    }
    
    public void execute(WorkspaceCommand command) {
        command.execute();
    }
    
}

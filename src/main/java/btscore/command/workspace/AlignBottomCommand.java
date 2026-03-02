package btscore.command.workspace;

import blocksmith.ui.AlignmentPolicy;
import btscore.command.WorkspaceCommand;
import btscore.workspace.WorkspaceSession;

/**
 *
 * @author JoostMeulenkamp
 */
public class AlignBottomCommand implements WorkspaceCommand {
    
    private final WorkspaceSession workspaceModel;
    
    public AlignBottomCommand( WorkspaceSession workspaceModel) {
        this.workspaceModel = workspaceModel;
    }
    
    @Override
    public boolean execute() {
        workspaceModel.alignmentService().align(AlignmentPolicy.Mode.BOTTOM);
        
        return true;
    }
    
}

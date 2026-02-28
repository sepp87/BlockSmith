package btscore.command.workspace;

import blocksmith.ui.AlignmentPolicy;
import btscore.command.WorkspaceCommand;
import btscore.workspace.WorkspaceModel;

/**
 *
 * @author JoostMeulenkamp
 */
public class AlignBottomCommand implements WorkspaceCommand {
    
    private final WorkspaceModel workspaceModel;
    
    public AlignBottomCommand( WorkspaceModel workspaceModel) {
        this.workspaceModel = workspaceModel;
    }
    
    @Override
    public boolean execute() {
        workspaceModel.alignmentService().align(AlignmentPolicy.Mode.BOTTOM);
        
        return true;
    }
    
}

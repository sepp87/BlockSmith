package blocksmith.app.block.command;

import blocksmith.domain.block.BlockId;
import blocksmith.app.workspace.WorkspaceCommand;
import blocksmith.app.workspace.WorkspaceSession;

/**
 *
 * @author JoostMeulenkamp
 */
public class UpdateParamValueCommand implements WorkspaceCommand {

    private final WorkspaceSession workspace;
    private final BlockId block;
    private final String valueId;
    private final String value;

    public UpdateParamValueCommand(WorkspaceSession workspace, BlockId block, String valueId, String value) {
        this.workspace = workspace;
        this.block = block;
        this.valueId = valueId;
        this.value = value;
    }

    @Override
    public boolean execute() {
        
        workspace.graphEditor().updateParamValue(block, valueId, value);
        return true;
    }
}

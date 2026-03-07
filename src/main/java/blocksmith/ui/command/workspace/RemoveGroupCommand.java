package blocksmith.ui.command.workspace;

import blocksmith.domain.group.GroupId;
import blocksmith.ui.workspace.WorkspaceSession;
import blocksmith.ui.command.WorkspaceCommand;

/**
 *
 * @author Joost
 */
public class RemoveGroupCommand implements WorkspaceCommand {

    private final WorkspaceSession workspace;
    private final GroupId id;

    public RemoveGroupCommand(WorkspaceSession workspace, GroupId group) {
        this.workspace = workspace;
        this.id = group;
    }

    @Override
    public boolean execute() {
        workspace.graphEditor().removeGroup(id);
 
        return true;

    }

}

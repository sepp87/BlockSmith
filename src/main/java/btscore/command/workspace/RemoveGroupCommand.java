package btscore.command.workspace;

import blocksmith.domain.group.GroupId;
import btscore.workspace.WorkspaceModel;
import btscore.command.WorkspaceCommand;

/**
 *
 * @author Joost
 */
public class RemoveGroupCommand implements WorkspaceCommand {

    private final WorkspaceModel workspace;
    private final GroupId id;

    public RemoveGroupCommand(WorkspaceModel workspace, GroupId group) {
        this.workspace = workspace;
        this.id = group;
    }

    @Override
    public boolean execute() {
        workspace.graphEditor().removeGroup(id);
 
        return true;

    }

}

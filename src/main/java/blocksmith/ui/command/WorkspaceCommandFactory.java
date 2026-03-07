package blocksmith.ui.command;

import blocksmith.ui.command.Command;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.Connection;
import blocksmith.domain.connection.PortRef;
import blocksmith.domain.group.GroupId;
import blocksmith.ui.command.workspace.UpdateSelectionCommand;
import blocksmith.ui.command.workspace.AddConnectionCommand;
import blocksmith.ui.command.workspace.RemoveConnectionCommand;
import blocksmith.ui.command.workspace.RemoveGroupCommand;
import blocksmith.ui.command.workspace.MoveBlocksCommand;
import blocksmith.ui.command.workspace.RenameBlockCommand;
import blocksmith.ui.command.workspace.ResizeBlockCommand;
import blocksmith.ui.command.workspace.UpdateParamValueCommand;
import blocksmith.ui.workspace.FxWorkspaceRegistry;
import blocksmith.ui.workspace.WorkspaceSession;
import java.util.Collection;
import javafx.geometry.Point2D;

/**
 *
 * @author joostmeulenkamp
 */
public class WorkspaceCommandFactory {

    private final WorkspaceSession session;

    public WorkspaceCommandFactory(WorkspaceSession session) {
        this.session = session;
    }

    public Command createAddConnectionCommand(PortRef from, PortRef to) {
        return new AddConnectionCommand(session, from, to);
    }

    public Command createRemoveConnectionCommand(Connection connection) {
        return new RemoveConnectionCommand(session, connection);
    }

    public Command createRemoveGroupCommand(GroupId group) {
        return new RemoveGroupCommand(session, group);
    }

    public Command createUpdateSelectionCommand(BlockId block, boolean isModifierDown) {
        return new UpdateSelectionCommand(session, block, isModifierDown);
    }

    public Command createMoveBlocksCommand(Collection<BlockId> blocks, Point2D delta) {
        return new MoveBlocksCommand(session, blocks, delta);
    }

    public Command createResizeBlockCommand(BlockId block, double width, double height) {
        return new ResizeBlockCommand(session, block, width, height);
    }

    public Command createUpdateParamValueCommand(BlockId block, String valueId, String value) {
        return new UpdateParamValueCommand(session, block, valueId, value);
    }

    public Command createRenameBlockCommand(BlockId block, String label) {
        return new RenameBlockCommand(session, block, label);
    }
}

package blocksmith.app.workspace;

import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.Connection;
import blocksmith.domain.connection.PortRef;
import blocksmith.domain.group.GroupId;
import blocksmith.app.block.command.UpdateSelectionCommand;
import blocksmith.app.connection.command.AddConnectionCommand;
import blocksmith.app.connection.command.RemoveConnectionCommand;
import blocksmith.app.group.command.RemoveGroupCommand;
import blocksmith.app.block.command.MoveBlocksCommand;
import blocksmith.app.block.command.RenameBlockCommand;
import blocksmith.app.block.command.ResizeBlockCommand;
import blocksmith.app.block.command.UpdateParamValueCommand;
import blocksmith.app.workspace.WorkspaceSession;
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

    public Command createMoveBlocksCommand(Collection<BlockId> blocks, double dx, double dy) {
        return new MoveBlocksCommand(session, blocks, dx, dy);
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

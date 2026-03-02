package btscore.workspace;

import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.Connection;
import blocksmith.domain.group.GroupId;
import java.util.Collection;

/**
 *
 * @author joost
 */
public record GraphProjectionDiff(
        Collection<BlockId> addedBlocks,
        Collection<BlockId> removedBlocks,
        Collection<BlockId> updatedBlocks,
        Collection<Connection> addedConnections,
        Collection<Connection> removedConnections,
        Collection<GroupId> addedGroups,
        Collection<GroupId> removedGroups,
        Collection<GroupId> updatedGroups) {

}

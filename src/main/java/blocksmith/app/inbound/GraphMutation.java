package blocksmith.app.inbound;

import blocksmith.domain.block.BlockPosition;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.Connection;
import blocksmith.domain.connection.PortRef;
import java.util.Collection;

/**
 *
 * @author joostmeulenkamp
 */
public interface GraphMutation {

    void addBlock(String type, double x, double y);

    void removeBlock(BlockId id);

    void removeAllBlocks(Collection<BlockId> ids);

    void updateParamValue(BlockId id, String valueId, String value);

    void moveBlocks(Collection<BlockPosition> requests);
    
    void resizeBlock(BlockId id, double width, double height);

    void addConnection(PortRef from, PortRef to);

    void removeConnection(Connection connection);

    void addGroup(String label, Collection<BlockId> blocks);

    void removeGroup(String label, Collection<BlockId> blocks);

//    void removeGroup();
//    void ungroup(BlockId id);
}

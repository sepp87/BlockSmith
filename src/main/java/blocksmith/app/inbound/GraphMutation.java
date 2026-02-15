package blocksmith.app.inbound;

import blocksmith.app.block.MoveBlockRequest;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.block.EditorMetadata;
import blocksmith.domain.connection.Connection;
import blocksmith.domain.connection.PortRef;
import java.util.Collection;

/**
 *
 * @author joostmeulenkamp
 */
public interface GraphMutation {

    void addBlock(String type, EditorMetadata metadata);

    void removeBlock(BlockId id);
    
    void removeAllBlocks(Collection<BlockId> ids);

    void setParamValue(BlockId id, String valueId, String value);
    
    void moveBlocks(Collection<MoveBlockRequest> requests);

    void addConnection(PortRef from, PortRef to);

    void removeConnection(Connection connection);

    void addGroup(String label, Collection<BlockId> blocks);

//    void removeGroup();
//    void ungroup(BlockId id);
}

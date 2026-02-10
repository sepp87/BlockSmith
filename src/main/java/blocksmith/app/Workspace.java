package blocksmith.app;

import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.Connection;
import blocksmith.domain.connection.PortRef;
import blocksmith.domain.graph.Graph;
import java.util.Collection;

/**
 *
 * @author joost
 */
public class Workspace {

    private Graph graph;
    private final AddBlock addBlock;
    private final RemoveBlock removeBlock;
    private final AddConnection addConnection;
    private final RemoveConnection removeConnection;
    private final AddGroup addGroup;
    private final RemoveGroup removeGroup;

    public Workspace(
            Graph graph,
            AddBlock addBlock,
            RemoveBlock removeBlock,
            AddConnection addConnection,
            RemoveConnection removeConnection,
            AddGroup addGroup,
            RemoveGroup removeGroup) {

        this.graph = graph;
        this.addBlock = addBlock;
        this.removeBlock = removeBlock;
        this.addConnection = addConnection;
        this.removeConnection = removeConnection;
        this.addGroup = addGroup;
        this.removeGroup = removeGroup;
    }

    public void addBlock(String type) {
        var id = BlockId.create();
        graph = addBlock.execute(graph, id, type);
    }
    
    public void removeBlock(BlockId id) {
        
    }
    
    public void addConnection(PortRef from, PortRef to) {
        
    }
    
    public void removeConnection(Connection connection) {
        
    }
    
    public void addGroup(Collection<BlockId> blocks) {
        
    }
    
    public void removeGroup() {
        
    }

}

package blocksmith.domain.graph;

import blocksmith.domain.block.Block;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author joost
 */
public final class Graph {
    
   private final List<Block> blocks;
   private final List<Connection> connections;
   
   public Graph(List<Block> blocks, List<Connection> connections) {
       this.blocks = List.copyOf(Objects.requireNonNull(blocks));
       this.connections = List.copyOf(Objects.requireNonNull(connections));
   }
   
   public Collection<Block> blocks() {
       return blocks;
   }
   
   public Collection<Connection> connections() {
       return connections;
   }
    
}

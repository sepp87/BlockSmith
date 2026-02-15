package blocksmith.app.group;

import blocksmith.domain.block.BlockId;
import blocksmith.domain.graph.Graph;
import blocksmith.domain.group.Group;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author joost
 */
public class AddGroup {

    
    public Graph execute(Graph graph, String label, Collection<BlockId> blocks) {
        var group = new Group(label, List.copyOf(blocks));
        return graph.withGroup(group);
    }
}

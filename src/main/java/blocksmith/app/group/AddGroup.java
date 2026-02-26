package blocksmith.app.group;

import blocksmith.domain.block.BlockId;
import blocksmith.domain.graph.Graph;
import blocksmith.domain.group.Group;
import blocksmith.domain.group.GroupId;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author joost
 */
public class AddGroup {

    
    public Graph execute(Graph graph, GroupId id, String label, Collection<BlockId> blocks) {
        var group = new Group(id, label, List.copyOf(blocks));
        return graph.withGroup(group);
    }
}

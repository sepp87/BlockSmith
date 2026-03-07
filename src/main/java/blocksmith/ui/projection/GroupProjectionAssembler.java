package blocksmith.ui.projection;

import blocksmith.domain.block.BlockId;
import blocksmith.domain.group.Group;
import blocksmith.domain.group.GroupId;
import blocksmith.ui.graph.block.BlockModel;
import blocksmith.ui.graph.group.BlockGroupModel;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author joost
 */
public class GroupProjectionAssembler {

    public Map<GroupId, BlockGroupModel> create(Collection<Group> groups, Map<BlockId, BlockModel> blockIndex) {
        var result = new HashMap<GroupId, BlockGroupModel>();
        for (var group : groups) {
            var model = new BlockGroupModel(group.id().toString());
            model.labelProperty().set(group.label());
            group.blocks().forEach(b -> model.addBlock(blockIndex.get(b)));
            result.put(group.id(), model);
        }
        return Map.copyOf(result);
    }
}

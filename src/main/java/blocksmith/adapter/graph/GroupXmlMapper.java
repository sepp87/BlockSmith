package blocksmith.adapter.graph;

import blocksmith.domain.block.BlockId;
import blocksmith.domain.group.Group;
import blocksmith.xml.v2.BlockRefXml;
import blocksmith.xml.v2.GroupXml;
import blocksmith.xml.v2.GroupsXml;
import blocksmith.xml.v2.ObjectFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author joost
 */
public class GroupXmlMapper {

    private final ObjectFactory xmlFactory;

    public GroupXmlMapper(ObjectFactory xmlFactory) {
        this.xmlFactory = xmlFactory;
    }

    public List<Group> toDomain(List<GroupXml> groupsXml) {
        var result = new ArrayList<Group>();
        for (var groupXml : groupsXml) {
            var label = groupXml.getLabel();
            var blocks = groupXml.getBlock().stream().map(ref -> BlockId.from(ref.getId())).toList();
            var group = new Group(label, blocks);
            result.add(group);
        }
        return result;
    }

    public GroupsXml toXml(Collection<Group> groups) {
        var result = xmlFactory.createGroupsXml();
        
        for(var group : groups) {
            var groupXml = xmlFactory.createGroupXml();
            groupXml.setLabel(group.label());
            var blocks = blockRefsToXml(group.blocks());
            groupXml.getBlock().addAll(blocks);
        }
        return result;

    }
    
    public List<BlockRefXml> blockRefsToXml(Collection<BlockId> blocks) {
        var result = new ArrayList<BlockRefXml>();
        for(var id : blocks) {
            var blockRefXml = xmlFactory.createBlockRefXml();
            blockRefXml.setId(id.toString());
            result.add(blockRefXml);
        }
        return result;
    }

}

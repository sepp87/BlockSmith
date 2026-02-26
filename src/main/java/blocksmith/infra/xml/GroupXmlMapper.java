package blocksmith.infra.xml;

import blocksmith.domain.block.BlockId;
import blocksmith.domain.group.Group;
import blocksmith.domain.group.GroupId;
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
            var id = GroupId.create();
            var label = groupXml.getLabel();
            var blocks = groupXml.getBlock().stream().map(ref -> BlockId.from(ref.getId())).toList();
            var group = new Group(id, label, blocks);
            result.add(group);
        }
        return result;
    }

    public GroupsXml toXml(Collection<Group> groups) {
        var result = xmlFactory.createGroupsXml();

        for (var group : groups) {
            var groupXml = xmlFactory.createGroupXml();
            groupXml.setLabel(group.label());
            var blocks = blockRefsToXml(group.blocks());
            groupXml.getBlock().addAll(blocks);
        }
        return result;

    }

    public List<BlockRefXml> blockRefsToXml(Collection<BlockId> blocks) {
        var result = new ArrayList<BlockRefXml>();
        for (var id : blocks) {
            var blockRefXml = xmlFactory.createBlockRefXml();
            blockRefXml.setId(id.toString());
            result.add(blockRefXml);
        }
        return result;
    }

}

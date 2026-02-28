package btscore.graph.group;

import blocksmith.domain.block.BlockId;
import blocksmith.domain.group.Group;
import java.util.Collection;
import java.util.HashSet;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.ReadOnlySetWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import btsxml.BlockReferenceTag;
import btsxml.GroupTag;
import btsxml.ObjectFactory;
import btscore.graph.block.BlockModel;
import btscore.graph.base.BaseModel;
import static btscore.graph.io.GraphSaver.getObjectFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.collections.ObservableMap;

/**
 *
 * @author JoostMeulenkamp
 */
public class BlockGroupModel extends BaseModel {

    private final Map<BlockId, BlockModel> blocks = new HashMap<>();
    private final ObservableSet<BlockModel> internalBlocks = FXCollections.observableSet();
    private final ObservableSet<BlockModel> readonlyBlocks = FXCollections.unmodifiableObservableSet(internalBlocks);

    public BlockGroupModel(String id) {
        this.id.set(id);
        labelProperty().set("Name group here...");
    }

    public void updateFrom(Group group, Map<BlockId, BlockModel> blockIndex) {
        var oldBlocks = new ArrayList<>(internalBlocks.stream().map(b -> BlockId.from(b.getId())).toList());
        var newBlocks = new ArrayList<>(group.blocks());

        var addedToGroup = new ArrayList<BlockId>();
        for (var n : newBlocks) {
            var contained = oldBlocks.remove(n);
            if (!contained) {
                addedToGroup.add(n);
            }
        }
        for (var block : addedToGroup) {
            blocks.put(block, blockIndex.get(block));
            internalBlocks.add(blockIndex.get(block));
        }

        var removedFromGroup = oldBlocks;
        for (var block : removedFromGroup) {
            var projection = blocks.remove(block);
            internalBlocks.remove(projection);
        }
    }

    public void setBlocks(Collection<BlockModel> blocks) {
        for (BlockModel blockModel : blocks) {
            addBlock(blockModel);
        }
    }

    public void addBlock(BlockModel blockModel) {
        internalBlocks.add(blockModel);
        blocks.put(BlockId.from(blockModel.getId()), blockModel);
    }

    public void removeBlock(BlockModel blockModel) {
        internalBlocks.remove(blockModel);
        blocks.remove(BlockId.from(blockModel.getId()), blockModel);
    }

    // return set as immutable
    public ObservableSet<BlockModel> getBlocks() {
        return readonlyBlocks;
    }

    @Override
    public void dispose() {
        for (BlockModel blockModel : new HashSet<>(internalBlocks)) {
            removeBlock(blockModel);
        }
        super.dispose();
    }

    public void serialize(GroupTag xmlTag) {
        ObjectFactory factory = getObjectFactory();
        xmlTag.setName(labelProperty().get());
        for (BlockModel block : internalBlocks) {
            BlockReferenceTag blockReferenceTag = factory.createBlockReferenceTag();
            blockReferenceTag.setUUID(block.idProperty().get());
            xmlTag.getBlockReference().add(blockReferenceTag);
        }
    }

    public void deserialize(GroupTag xmlTag) {

    }
}

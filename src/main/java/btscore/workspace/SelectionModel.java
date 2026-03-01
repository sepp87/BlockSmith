package btscore.workspace;

import blocksmith.app.inbound.GraphEditor;
import blocksmith.domain.block.Block;
import blocksmith.domain.block.BlockId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 *
 * @author joost
 */
public class SelectionModel {

    private final GraphEditor editor;

    private Set<BlockId> selected = Set.of();
    private List<Consumer<Collection<BlockId>>> listeners = new ArrayList<>();

//    public SelectionModel (GraphProjection graph) {
    public SelectionModel(GraphEditor editor) {
        this.editor = editor;
    }

    public Collection<BlockId> selected() {
        return selected;
    }

    public void select(Collection<BlockId> blocks) {
        selected = Set.copyOf(blocks);
        selectionChanged();
    }

    public void toggle(BlockId id) {
        var updated = new HashSet<BlockId>(selected);
        if (updated.contains(id)) {
            updated.remove(id);
        } else {
            updated.add(id);
        }
        select(updated);
    }

    public void deselectAll() {
        select(List.of());
    }

    public void selectAll() {
        var ids = editor.graphSnapshot().blocks().stream().map(Block::id).toList();
        select(ids);
    }

    public void setOnSelectionChanged(Consumer<Collection<BlockId>> listener) {
        listeners.add(listener);
    }

    private void selectionChanged() {
        listeners.forEach(c -> c.accept(selected));
    }

}

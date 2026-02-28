
package btscore.workspace;

import blocksmith.domain.block.BlockId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 *
 * @author joost
 */
public class SelectionModel {

    private Set<BlockId> selected = Set.of();
    private List<Consumer<Collection<BlockId>>> listeners = new ArrayList<>();
    
    
    public Collection<BlockId> selected() {
        return selected;
    }
    
    public void setSelected(Collection<BlockId> blocks) {
        selected = Set.copyOf(blocks);
        selectionChanged();
    }
    
    public void setOnSelectionChanged(Consumer<Collection<BlockId>> listener) {
        listeners.add(listener);
    }
    
    private void selectionChanged() {
        listeners.forEach(c -> c.accept(selected));
    }
    
    
}

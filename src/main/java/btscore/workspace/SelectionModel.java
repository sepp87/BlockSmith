
package btscore.workspace;

import blocksmith.domain.block.BlockId;
import java.util.List;

/**
 *
 * @author joost
 */
public class SelectionModel {

    private List<BlockId> selected = List.of();
    
    public List<BlockId> selected() {
        return selected;
    }
    
    public void setSelected(List<BlockId> blocks) {
        selected = List.copyOf(blocks);
    }
    
}

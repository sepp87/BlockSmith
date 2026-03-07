package blocksmith.ui.graph.connection;

import blocksmith.ui.graph.block.BlockModel;

/**
 *
 * @author JoostMeulenkamp
 */
public class CopiedConnection {

    public BlockModel oldBlock;
    public BlockModel newBlock;
    
    public CopiedConnection(BlockModel oldBlock, BlockModel newBlock){
        this.oldBlock = oldBlock;
        this.newBlock = newBlock;
    }
}

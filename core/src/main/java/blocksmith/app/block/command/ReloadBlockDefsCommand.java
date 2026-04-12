package blocksmith.app.block.command;

import blocksmith.app.command.AppCommand;
import blocksmith.app.block.BlockLibrary;

/**
 *
 * @author joostmeulenkamp
 */
public class ReloadBlockDefsCommand implements AppCommand {

    private final BlockLibrary blockLibrary;
    
    public ReloadBlockDefsCommand(BlockLibrary blockLibrary) {
        this.blockLibrary = blockLibrary;
    }

    @Override
    public boolean execute() {
        blockLibrary.reload();
        return true;

    }

}

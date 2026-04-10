package blocksmith.app.block.command;

import blocksmith.app.block.BlockLibraryService;
import blocksmith.app.command.AppCommand;

/**
 *
 * @author joostmeulenkamp
 */
public class ReloadBlockDefsCommand implements AppCommand {

    private final BlockLibraryService blockLibrary;
    
    public ReloadBlockDefsCommand(BlockLibraryService blockLibrary) {
        this.blockLibrary = blockLibrary;
    }

    @Override
    public boolean execute() {
        blockLibrary.reload();
        return true;

    }

}

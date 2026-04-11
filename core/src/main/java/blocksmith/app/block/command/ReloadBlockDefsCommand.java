package blocksmith.app.block.command;

import blocksmith.infra.blockloader.ScannedBlockLibrary;
import blocksmith.app.command.AppCommand;

/**
 *
 * @author joostmeulenkamp
 */
public class ReloadBlockDefsCommand implements AppCommand {

    private final ScannedBlockLibrary blockLibrary;
    
    public ReloadBlockDefsCommand(ScannedBlockLibrary blockLibrary) {
        this.blockLibrary = blockLibrary;
    }

    @Override
    public boolean execute() {
        blockLibrary.reload();
        return true;

    }

}

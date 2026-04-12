package blocksmith.infra.blockloader;

import blocksmith.app.outbound.BlockScanner;

/**
 *
 * @author joost
 */
public record CompositeBlockScanner(
        ClassScanner classScanner,
        MethodBlockScanner functionalBlocks,
        SourceBlockScanner sourceBlocks) implements BlockScanner {

    public void rescan() {
        classScanner.rescan();
        functionalBlocks.rescan();
        sourceBlocks.rescan();
    }

}

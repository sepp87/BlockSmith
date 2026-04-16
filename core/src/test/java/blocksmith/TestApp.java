
package blocksmith;

import blocksmith.app.block.BlockDefLibrary;
import blocksmith.app.block.BlockLibrary;
import blocksmith.domain.block.BlockFactory;
import blocksmith.infra.blockloader.ClassScanner;
import blocksmith.infra.blockloader.CompositeBlockDefLoader;
import blocksmith.infra.blockloader.CompositeBlockExecLoader;
import blocksmith.infra.blockloader.CompositeBlockScanner;
import blocksmith.infra.blockloader.MethodBlockDefLoader;
import blocksmith.infra.blockloader.MethodBlockFuncLoader;
import blocksmith.infra.blockloader.MethodBlockScanner;
import blocksmith.infra.blockloader.SourceBlockDefLoader;
import blocksmith.infra.blockloader.SourceBlockExecLoader;
import blocksmith.infra.blockloader.SourceBlockScanner;
import java.io.IOException;

/**
 *
 * @author joost
 */
public class TestApp {

    private final BlockLibrary blockLibrary;
    private final BlockFactory blockFactory;

    public TestApp() throws IOException {

        var env = Environment.test();
        
        var classScanner = ClassScanner.forTest();
        var methodBlockScanner = new MethodBlockScanner(classScanner);
        var sourceBlockScanner = new SourceBlockScanner(classScanner);
        var compositeBlockScanner = new CompositeBlockScanner(classScanner, methodBlockScanner, sourceBlockScanner);

        var methodDefLoader = new MethodBlockDefLoader(methodBlockScanner);
        var sourceBlockDefLoader = new SourceBlockDefLoader(sourceBlockScanner);
        var compositeDefLoader = new CompositeBlockDefLoader(methodDefLoader, sourceBlockDefLoader);

        var sourceBlockExecLoader = new SourceBlockExecLoader(sourceBlockScanner);
        var methodFuncLoader = new MethodBlockFuncLoader(methodBlockScanner);
        var compositeExecLoader = new CompositeBlockExecLoader(methodFuncLoader, sourceBlockExecLoader);

        this.blockLibrary = new BlockLibrary(compositeBlockScanner, compositeDefLoader, compositeExecLoader);
        

        this.blockFactory = new BlockFactory(blockLibrary);
    }

    public BlockFactory getBlockFactory() {
        return blockFactory;
    }

}

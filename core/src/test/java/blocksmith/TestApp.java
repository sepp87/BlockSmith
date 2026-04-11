
package blocksmith;

import blocksmith.app.block.BlockDefLibrary;
import blocksmith.domain.block.BlockFactory;
import blocksmith.infra.blockloader.ClassScanner;
import blocksmith.infra.blockloader.CompositeBlockDefLoader;
import blocksmith.infra.blockloader.MethodBlockDefLoader;
import blocksmith.infra.blockloader.MethodBlockScanner;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author joost
 */
public class TestApp {

    private final BlockDefLibrary blockDefLibrary;
    private final BlockFactory blockFactory;

    public TestApp() throws IOException {

        var env = Environment.test();
        var classIndex = new ClassScanner(env.paths().getLibDir());
        var methodIndex = new MethodBlockScanner(classIndex.classes());

        var methodDefLoader = new MethodBlockDefLoader(methodIndex.methods());
        var compositeDefLoader = new CompositeBlockDefLoader(List.of(methodDefLoader));
        this.blockDefLibrary = new BlockDefLibrary(compositeDefLoader.load());

        this.blockFactory = new BlockFactory(blockDefLibrary);
    }

    public BlockFactory getBlockFactory() {
        return blockFactory;
    }

}

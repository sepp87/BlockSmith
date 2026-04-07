
package blocksmith;

import blocksmith.app.block.BlockDefLibrary;
import blocksmith.domain.block.BlockFactory;
import blocksmith.infra.AppPaths;
import blocksmith.infra.blockloader.ClassIndex;
import blocksmith.infra.blockloader.CompositeBlockDefLoader;
import blocksmith.infra.blockloader.MethodBlockDefLoader;
import blocksmith.infra.blockloader.MethodIndex;
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

        var paths = new AppPaths();
        var classIndex = new ClassIndex(paths);
        var methodIndex = new MethodIndex(classIndex.classes());

        var methodDefLoader = new MethodBlockDefLoader(methodIndex.methods());
        var compositeDefLoader = new CompositeBlockDefLoader(List.of(methodDefLoader));
        this.blockDefLibrary = new BlockDefLibrary(compositeDefLoader.load());

        this.blockFactory = new BlockFactory(blockDefLibrary);
    }

    public BlockFactory getBlockFactory() {
        return blockFactory;
    }

}

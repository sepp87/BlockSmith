package btscore;

import blocksmith.adapter.AppPaths;
import blocksmith.adapter.block.ClassIndex;
import blocksmith.adapter.block.MethodIndex;
import blocksmith.adapter.block.MethodBlockDefLoader;
import blocksmith.adapter.block.CompositeBlockDefLoader;
import blocksmith.adapter.block.MethodBlockFuncLoader;
import blocksmith.app.BlockDefLibrary;
import blocksmith.app.BlockFuncLibrary;
import blocksmith.ui.BlockModelFactory;
import btscore.graph.block.BlockLibraryLoader;
import java.io.IOException;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 *
 * @author joost
 */
public class App {

    private final BlockDefLibrary blockDefLibrary;
    private final BlockFuncLibrary blockFuncLibrary;
    
    public App() throws IOException {
        configureLogging();

        var paths = new AppPaths();
        var classIndex = new ClassIndex(paths);
        var methodIndex = new MethodIndex(classIndex.classes());
        
        var methodDefLoader = new MethodBlockDefLoader(methodIndex.methods());
        var compositeDefLoader = new CompositeBlockDefLoader(List.of(methodDefLoader));
        this.blockDefLibrary = new BlockDefLibrary(compositeDefLoader.load());
        
        var methodFuncLoader = new MethodBlockFuncLoader(methodIndex.methods());
        this.blockFuncLibrary = new BlockFuncLibrary(methodFuncLoader.load());
        
        var blockModelFactory = new BlockModelFactory(blockDefLibrary, blockFuncLibrary);
        
        //Load all block types
        BlockLibraryLoader.loadBlocks();
        System.out.println("Launcher.main() Number of loaded blocks is " + BlockLibraryLoader.BLOCK_TYPE_LIST.size());

    }

    private void configureLogging() {
        Logger root = Logger.getLogger("btscore");
        root.setLevel(Level.INFO);
        root.setUseParentHandlers(false);

        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.INFO);
        handler.setFormatter(new Formatter() {

            @Override
            public synchronized String format(LogRecord r) {
                String formattedMessage = formatMessage(r); // <── this expands {0}, {1}, etc.
                String fullName = r.getLoggerName();
                String simpleName = fullName.substring(fullName.lastIndexOf('.') + 1);

                return String.format(
                        "%tF %<tT [%s] %s - %s%n",
                        r.getMillis(),
                        r.getLevel(),
                        simpleName,
                        formattedMessage
                );
            }
        });
        root.addHandler(handler);
    }

    public BlockDefLibrary getBlockDefLibrary() {
        return blockDefLibrary;
    }
    
    public BlockFuncLibrary getBlockFuncLibrary() {
        return blockFuncLibrary;
    }
    
}

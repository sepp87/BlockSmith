package btscore;

import blocksmith.adapter.block.MethodClassProvider;
import blocksmith.adapter.block.MethodBlockDefLoader;
import blocksmith.adapter.block.CompositeBlockDefLoader;
import blocksmith.app.BlockDefLibrary;
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

    public App() throws IOException {
        configureLogging();

        var libraryProvider = new MethodClassProvider();
        var internalMethodLoader = new MethodBlockDefLoader(libraryProvider.internalMethodLibraries());
        var externalMethodLoader = new MethodBlockDefLoader(libraryProvider.externalMethodLibraries());
        var blockDefLoader = new CompositeBlockDefLoader(List.of(internalMethodLoader, externalMethodLoader));
        var blockDefLibrary = new BlockDefLibrary(blockDefLoader.load());
        
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

}

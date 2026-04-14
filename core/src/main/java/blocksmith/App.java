package blocksmith;

import blocksmith.app.block.AddBlock;
import blocksmith.app.connection.AddConnection;
import blocksmith.app.group.AddGroup;
import blocksmith.infra.blockloader.ClassScanner;
import blocksmith.infra.blockloader.MethodBlockScanner;
import blocksmith.infra.blockloader.MethodBlockDefLoader;
import blocksmith.infra.blockloader.CompositeBlockDefLoader;
import blocksmith.infra.blockloader.MethodBlockFuncLoader;
import blocksmith.infra.xml.GraphXmlMapper;
import blocksmith.infra.xml.GraphXmlRepo;
import blocksmith.app.block.BlockDefLibrary;
import blocksmith.app.block.BlockExecLibrary;
import blocksmith.app.GraphEditorFactory;
import blocksmith.app.block.CopyBlocks;
import blocksmith.app.block.PasteBlocks;
import blocksmith.app.clipboard.CopyMemory;
import blocksmith.app.command.CommandRegistry;
import blocksmith.app.connection.RemoveConnection;
import blocksmith.app.block.BlockLibrary;
import blocksmith.app.outbound.AppScheduler;
import blocksmith.app.outbound.GraphRepo;
import blocksmith.domain.block.BlockFactory;
import blocksmith.exec.ExecutionSessionFactory;
import blocksmith.infra.blockloader.CompositeBlockExecLoader;
import blocksmith.infra.blockloader.CompositeBlockScanner;
import blocksmith.infra.blockloader.SourceBlockDefLoader;
import blocksmith.infra.blockloader.SourceBlockExecLoader;
import blocksmith.infra.blockloader.SourceBlockScanner;
import blocksmith.xml.v2.ObjectFactory;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Path;
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
    private final BlockExecLibrary blockFuncLibrary;
    private final BlockLibrary blockLibrary;
    private final GraphRepo graphRepo;
    private final GraphEditorFactory graphEditorFactory;
    private final ExecutionSessionFactory executionSessionFactory;
    private final CommandRegistry commandRegistry;

    public App(Path libDirectory, AppScheduler scheduler) throws IOException, JAXBException {
        configureLogging();

        var classScanner = new ClassScanner(libDirectory);
        var methodBlockScanner = new MethodBlockScanner(classScanner);
        var sourceBlockScanner = new SourceBlockScanner(classScanner);
        var compositeBlockScanner = new CompositeBlockScanner(classScanner, methodBlockScanner, sourceBlockScanner);

        var methodDefLoader = new MethodBlockDefLoader(methodBlockScanner);
        var sourceBlockDefLoader = new SourceBlockDefLoader(sourceBlockScanner);
        var compositeDefLoader = new CompositeBlockDefLoader(methodDefLoader, sourceBlockDefLoader);
        this.blockDefLibrary = new BlockDefLibrary(compositeDefLoader.load());

        var sourceBlockExecLoader = new SourceBlockExecLoader(sourceBlockScanner);
        var methodFuncLoader = new MethodBlockFuncLoader(methodBlockScanner);
        var compositeExecLoader = new CompositeBlockExecLoader(methodFuncLoader, sourceBlockExecLoader);
        this.blockFuncLibrary = new BlockExecLibrary(compositeExecLoader.load());

        this.blockLibrary = new BlockLibrary(compositeBlockScanner, compositeDefLoader, compositeExecLoader);

        var blockFactory = new BlockFactory(blockDefLibrary);
        var graphXmlMapper = new GraphXmlMapper(blockFactory);
        var jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
        this.graphRepo = new GraphXmlRepo(graphXmlMapper, jaxbContext);

        var addBlock = new AddBlock(blockFactory);
        var addConnection = new AddConnection();
        var removeConnection = new RemoveConnection();
        var addGroup = new AddGroup();
        var copyMemory = new CopyMemory();
        var copyBlocks = new CopyBlocks(copyMemory);
        var pasteBlocks = new PasteBlocks(copyMemory);
        this.graphEditorFactory = new GraphEditorFactory(
                addBlock,
                addConnection, removeConnection,
                addGroup,
                copyBlocks, pasteBlocks
        );

        this.executionSessionFactory = new ExecutionSessionFactory(blockDefLibrary, blockFuncLibrary, scheduler);

        this.commandRegistry = new CommandRegistry();

    }

    private void configureLogging() {
        Logger root = Logger.getLogger("blocksmith");
        root.setLevel(Level.FINEST);
        root.setUseParentHandlers(false);

        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.FINEST);
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

    public BlockLibrary getBlockLibrary() {
        return blockLibrary;
    }

    public GraphRepo getGraphRepo() {
        return graphRepo;
    }

    public GraphEditorFactory getGraphEditorFactory() {
        return graphEditorFactory;
    }

    public ExecutionSessionFactory getExecutionSessionFactory() {
        return executionSessionFactory;
    }

    public CommandRegistry getCommandRegistry() {
        return commandRegistry;
    }

}

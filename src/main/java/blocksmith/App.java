package blocksmith;

import blocksmith.app.block.AddBlock;
import blocksmith.app.connection.AddConnection;
import blocksmith.app.group.AddGroup;
import blocksmith.infra.AppPaths;
import blocksmith.infra.blockloader.ClassIndex;
import blocksmith.infra.blockloader.MethodIndex;
import blocksmith.infra.blockloader.MethodBlockDefLoader;
import blocksmith.infra.blockloader.CompositeBlockDefLoader;
import blocksmith.infra.blockloader.MethodBlockFuncLoader;
import blocksmith.infra.xml.GraphXmlMapper;
import blocksmith.infra.xml.GraphXmlRepo;
import blocksmith.app.block.BlockDefLibrary;
import blocksmith.app.block.BlockFuncLibrary;
import blocksmith.app.GraphEditorFactory;
import blocksmith.app.connection.RemoveConnection;
import blocksmith.app.group.RemoveGroup;
import blocksmith.app.outbound.GraphRepo;
import blocksmith.domain.block.BlockFactory;
import blocksmith.xml.v2.ObjectFactory;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
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
    private final GraphRepo graphRepo;
    private final GraphEditorFactory graphEditorFactory;

    public App() throws IOException, JAXBException {
        configureLogging();

        var paths = new AppPaths();
        var classIndex = new ClassIndex(paths);
        var methodIndex = new MethodIndex(classIndex.classes());

        var methodDefLoader = new MethodBlockDefLoader(methodIndex.methods());
        var compositeDefLoader = new CompositeBlockDefLoader(List.of(methodDefLoader));
        this.blockDefLibrary = new BlockDefLibrary(compositeDefLoader.load());

        var methodFuncLoader = new MethodBlockFuncLoader(methodIndex.methods());
        this.blockFuncLibrary = new BlockFuncLibrary(methodFuncLoader.load());

        var blockFactory = new BlockFactory(blockDefLibrary);
        var graphXmlMapper = new GraphXmlMapper(blockFactory);
        var jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
        this.graphRepo = new GraphXmlRepo(graphXmlMapper, jaxbContext);

        var addBlock = new AddBlock(blockFactory);
        var addConnection = new AddConnection();
        var removeConnection = new RemoveConnection();
        var addGroup = new AddGroup();
        var removeGroup = new RemoveGroup();
        this.graphEditorFactory = new GraphEditorFactory(
                addBlock, 
                addConnection, removeConnection,
                addGroup, removeGroup
        );

    }

    private void configureLogging() {
        Logger root = Logger.getLogger("blocksmith");
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

    public GraphRepo getGraphRepo() {
        return graphRepo;
    }

    public GraphEditorFactory getGraphEditorFactory() {
        return graphEditorFactory;
    }

}

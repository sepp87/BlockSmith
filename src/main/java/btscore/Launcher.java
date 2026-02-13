package btscore;

import blocksmith.App;
import btscore.graph.block.BlockLibraryLoader;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.util.IOUtils;
import org.xml.sax.SAXException;

//
/**
 *
 * @author joost
 */
public class Launcher {

    public static boolean GRAPH_LOADER_V2 = false;
    public static boolean VALUE_TYPE_MAPPER = false;
    public static boolean BLOCK_DEF_LOADER = false;

    public static void main(String[] args) throws IOException, OpenXML4JException, SAXException, Exception {

        boolean devMode = Boolean.getBoolean("blocksmith.dev") || "dev".equalsIgnoreCase(System.getenv("BLOCKSMITH_MODE")); // set flag -Dblocksmith.dev=true
        boolean hasConsole = System.console() != null;
        boolean isHeadless = GraphicsEnvironment.isHeadless();

        IOUtils.setByteArrayMaxOverride(300_000_000);
        App app = new App();

        if (devMode) {
            //            Draft.getGenericTypeOfMethodParam();

            GRAPH_LOADER_V2 = true;
            BLOCK_DEF_LOADER = true;
            if (!BLOCK_DEF_LOADER) {
                //Load all block types
                BlockLibraryLoader.loadBlocks();
                System.out.println("Launcher.main() Number of loaded blocks is " + BlockLibraryLoader.BLOCK_TYPE_LIST.size());
            }

            new UiAppRunner(app).run();

        } else if (hasConsole || isHeadless) {
            // run CLI

        } else {
            new UiAppRunner(app).run();
        }

    }

}

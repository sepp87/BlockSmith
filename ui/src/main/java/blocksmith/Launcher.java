package blocksmith;

import blocksmith.App;
import blocksmith.ui.UiAppRunner;
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

    public static boolean ON_DEV = false;

    public static void main(String[] args) throws IOException, OpenXML4JException, SAXException, Exception {

        boolean devMode = Boolean.getBoolean("blocksmith.dev") || "dev".equalsIgnoreCase(System.getenv("BLOCKSMITH_MODE")); // set flag -Dblocksmith.dev=true
        boolean hasConsole = System.console() != null;
        boolean isHeadless = GraphicsEnvironment.isHeadless();

        IOUtils.setByteArrayMaxOverride(300_000_000);
        App app = new App();

        if (devMode) {
            ON_DEV = true;
            Drafts.outputDefs();
            System.out.println("RUNNING IN DEV MODE");
            new UiAppRunner(app).run();

        } else if (hasConsole || isHeadless) {
            // run CLI

        } else {
            new UiAppRunner(app).run();
        }

    }

}

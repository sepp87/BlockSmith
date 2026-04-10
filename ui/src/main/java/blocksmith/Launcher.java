package blocksmith;

import blocksmith.App;
import blocksmith.ui.UiAppRunner;
import java.awt.GraphicsEnvironment;
import java.io.File;
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


    public static void main(String[] args) throws IOException, OpenXML4JException, SAXException, Exception {

        boolean devMode = Boolean.getBoolean("blocksmith.dev") || "dev".equalsIgnoreCase(System.getenv("BLOCKSMITH_MODE")); // set flag -Dblocksmith.dev=true
        boolean hasConsole = System.console() != null;
        boolean isHeadless = GraphicsEnvironment.isHeadless();

        IOUtils.setByteArrayMaxOverride(300_000_000);
        var env = devMode ? Environment.dev() : Environment.prod();
        var app = new App(env.paths().getLibDir());

        if (devMode) {
//            Drafts.outputDefs();
            System.out.println("RUNNING IN DEV MODE");

//            var path = new File("../btsxml/days-between-v2.btsxml").toPath();
            var path = new File("../btsxml/aslist-v2.btsxml").toPath();

            new UiAppRunner(app, env, path).run();

        } else if (hasConsole || isHeadless) {
            new UiAppRunner(app, env, null).run();

        } else {
            new UiAppRunner(app, env, null).run();
        }

    }

}

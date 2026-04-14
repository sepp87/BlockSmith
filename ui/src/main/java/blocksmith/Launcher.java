package blocksmith;

import blocksmith.ui.UiApp;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import javafx.application.Application;
import org.apache.poi.util.IOUtils;

//
/**
 *
 * @author joost
 */
public class Launcher {

    public static void main(String[] args) throws IOException  {

        boolean devMode = Boolean.getBoolean("blocksmith.dev") || "dev".equalsIgnoreCase(System.getenv("BLOCKSMITH_MODE")); // set flag -Dblocksmith.dev=true
        boolean hasConsole = System.console() != null;
        boolean isHeadless = GraphicsEnvironment.isHeadless();

        IOUtils.setByteArrayMaxOverride(300_000_000);
        var env = devMode ? Environment.dev() : Environment.prod();

        if (devMode) {
//            Drafts.outputDefs();
            System.out.println("RUNNING IN DEV MODE");

        } else if (hasConsole || isHeadless) {

        } else {

        }

//        var path = "../btsxml/days-between-v2.btsxml";
        var path = "../btsxml/aslist-v2.btsxml";
        
        UiApp.setEnv(env);
        Application.launch(UiApp.class, path);

    }
}

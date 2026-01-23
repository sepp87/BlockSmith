package btscore;

import java.io.IOException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.util.IOUtils;
import org.xml.sax.SAXException;

//
/**
 *
 * @author joostmeulenkamp
 */
public class Launcher {

    private final static boolean UI_MODE = true;

    public static void main(String[] args) throws IOException, OpenXML4JException, SAXException, Exception {

        IOUtils.setByteArrayMaxOverride(300_000_000);
        App app = new App();

        if (UI_MODE) {
            new UiAppRunner().run();
        } else {
            // run CLI
        }


    }

}

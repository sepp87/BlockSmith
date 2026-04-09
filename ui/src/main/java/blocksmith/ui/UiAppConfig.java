package blocksmith.ui;

import blocksmith.App;
import java.nio.file.Path;

/**
 *
 * @author joost
 */
public record UiAppConfig(
        App app,
        StylesheetService stylesheetService,
        Path initialDocument) {

}

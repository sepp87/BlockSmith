package blocksmith.ui;

import blocksmith.App;
import blocksmith.Environment;
import blocksmith.ui.UiApp;
import java.nio.file.Path;

/**
 *
 * @author joostmeulenkamp
 */
public class UiAppRunner {

    private static final String RESOURCES_DIRECTORY = "src/main/resources/";
    private final UiAppConfig config;

    public UiAppRunner(App app, Environment env, Path initialDocument) {
        
        var stylesheetService
                = env.isDev()
                ? StylesheetService.forDev(RESOURCES_DIRECTORY)
                : StylesheetService.forProd();
        
        var userPrefsService 
                = env.isDev()
                ? UserPrefsService.forDev(initialDocument)
                : UserPrefsService.forProd();
        
        this.config = new UiAppConfig(
                app,
                stylesheetService,
                userPrefsService
        );
    }

    public void run() {
        //Launch the UI
        UiApp.configure(config);
        UiApp.launch(UiApp.class);
    }
}

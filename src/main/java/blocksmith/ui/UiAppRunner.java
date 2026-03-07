package blocksmith.ui;


import blocksmith.App;
import blocksmith.ui.UiApp;

/**
 *
 * @author joostmeulenkamp
 */
public class UiAppRunner {

    private final App app;
    
    public UiAppRunner(App app) {
        this.app = app;
    }
    
    public void run() {
        //Launch the UI
        UiApp.setApp(app);
        UiApp.launch(UiApp.class);
    }
}

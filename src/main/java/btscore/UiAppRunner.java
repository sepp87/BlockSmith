package btscore;


import btscore.UiApp;

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
        UiApp.launch(UiApp.class);
    }
}

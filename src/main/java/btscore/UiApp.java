package btscore;

import blocksmith.App;
import blocksmith.ui.BlockModelFactory;
import blocksmith.ui.Workspace;
import btscore.editor.context.EditorContext;
import blocksmith.ui.WorkspaceSession;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import btscore.workspace.WorkspaceContext;
import btscore.editor.context.EditorEventRouter;
import btscore.editor.EditorController;
import btscore.editor.EditorView;
import btscore.editor.KeyboardController;
import btscore.editor.MenuBarController;
import btscore.editor.MenuBarView;
import btscore.editor.PanController;
import btscore.editor.SelectionRectangleController;
import btscore.editor.SelectionRectangleView;
import btscore.editor.radialmenu.RadialMenuController;
import btscore.editor.radialmenu.RadialMenuView;
import btscore.workspace.WorkspaceController;
import btscore.workspace.WorkspaceModel;
import btscore.editor.ZoomController;
import btscore.editor.ZoomView;
import btscore.editor.BlockSearchController;
import btscore.editor.BlockSearchView;
import btscore.editor.context.ActionManager;
import btscore.editor.context.CommandFactory;
import btscore.workspace.WorkspaceState;
import btscore.workspace.WorkspaceView;
import btscore.graph.io.GraphLoader;
import btscore.workspace.WorkspaceHistory;

/**
 *
 * @author joostmeulenkamp
 */
public class UiApp extends Application {

    private static App app;
    private static BlockModelFactory blockModelFactory;

    public static BlockModelFactory getBlockModelFactory() {
        return blockModelFactory;
    }

    public static void setApp(App app) {
        UiApp.app = app;
    }

    public static final boolean LOG_POTENTIAL_BUGS = true;
    public static final boolean LOG_METHOD_CALLS = false;
    public static final boolean LOG_EDITOR_STATE = false;

    public static final boolean TYPE_SENSITIVE = true;
    public static final boolean CONNECTION_REFACTOR = false;

    private static final double APP_WIDTH = 800;
    private static final double APP_HEIGHT = 800;
    private static Stage stage;

    @Override
    public void start(Stage stage) throws Exception {

        var blockDefLibrary = app.getBlockDefLibrary();
        var blockFuncLibrary = app.getBlockFuncLibrary();
        this.blockModelFactory = new BlockModelFactory(blockDefLibrary, blockFuncLibrary);

        this.stage = stage;
        stage.setTitle("BlockSmith: Blocks to Script");

        // Initialize models
        WorkspaceModel workspaceModel = new WorkspaceModel();

        var designSession = app.getGraphDesignSession();
        var workspaceSession = new WorkspaceSession(designSession, workspaceModel);

        // Initialize views
        WorkspaceView workspaceView = new WorkspaceView();
        BlockSearchView blockSearchView = new BlockSearchView();
        SelectionRectangleView selectionRectangleView = new SelectionRectangleView();
        ZoomView zoomView = new ZoomView();
        RadialMenuView radialMenuView = new RadialMenuView();
        MenuBarView menuBarView = new MenuBarView();
        EditorView editorView = new EditorView(radialMenuView, workspaceView, menuBarView, zoomView, selectionRectangleView, blockSearchView);

        EditorContext editorContext = new EditorContext(editorView);

        // initialize EventRouter for Context
        EditorEventRouter eventRouter = new EditorEventRouter();

        // Initialize controllers
        var zoomController = new ZoomController(eventRouter, editorContext, workspaceModel, zoomView);
        var blockSearchController = new BlockSearchController(eventRouter, editorContext, blockSearchView, blockDefLibrary);
        var selectionRectangleController = new SelectionRectangleController(eventRouter, editorContext, selectionRectangleView);
        var panController = new PanController(eventRouter, editorContext);
        var radialMenuController = new RadialMenuController(eventRouter, editorContext, radialMenuView);
        var menuBarController = new MenuBarController(editorContext, menuBarView);
        var editorController = new EditorController(eventRouter, editorView);

        // initialize ActionManager for Context
        var workspaceState = new WorkspaceState();
        var workspaceHistory = WorkspaceHistory.create();
        var commandFactory = new CommandFactory(editorContext, blockModelFactory);
        var actionManager = new ActionManager(editorContext, workspaceSession, commandFactory);
        var workspaceController = new WorkspaceController(workspaceModel, workspaceView);
        var workspaceContext = WorkspaceContext.create(workspaceController, workspaceSession, workspaceState, workspaceHistory, actionManager, commandFactory);
        workspaceController.bindContext(workspaceContext);
        editorContext.addWorkspace(workspaceContext);

        // Setup scene
        Scene scene = new Scene(editorView, APP_WIDTH, APP_HEIGHT);
        stage.setScene(scene);
        stage.show();
        stage.setFullScreen(false);

//        GraphLoader.deserialize(new File("btsxml/method-block.btsxml"), workspaceModel);
        GraphLoader.deserialize(new File("btsxml/aslist.btsxml"), workspaceModel);
//        GraphLoader.deserialize(new File("btsxml/addition.btsxml"), workspaceModel);
//        GraphLoader.deserialize(new File("btsxml/file.btsxml"), workspaceModel);
//        GraphLoader.deserialize(new File("btsxml/string-to-text.btsxml"), workspaceModel);
        editorView.printMenuBarHeight();

        Config.setStylesheetToScene(scene);
        stage.setOnCloseRequest(event -> {
            System.out.println("Closing application...");
            System.exit(0);  // Force JVM shutdown, triggering the shutdown hook
        });

        var keyboardController = new KeyboardController(editorContext);
        scene.setOnKeyPressed(event -> keyboardController.handleShortcutTriggered(event));

        if (Config.showHelpOnStartup()) {
            HelpDialog.show();
        }

    }

    public static Stage getStage() {
        return stage;
    }

}

package btscore;

import blocksmith.App;
import blocksmith.ui.BlockModelFactory;
import blocksmith.ui.Editor;
import blocksmith.ui.workspace.SaveDocument;
import blocksmith.ui.workspace.WorkspaceFactoryNew;
import btscore.editor.context.EditorContext;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;

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
import btscore.editor.ZoomController;
import btscore.editor.ZoomView;
import btscore.editor.BlockSearchController;
import btscore.editor.BlockSearchView;
import btscore.editor.context.ActionManager;
import btscore.editor.context.CommandFactory;
import btscore.workspace.WorkspaceFactory;
import btscore.workspace.WorkspaceView;

/**
 *
 * @author joostmeulenkamp
 */
public class UiApp extends Application {

    private static App app;

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
        var graphRepo = app.getGraphRepo();
        var graphEditorFactory = app.getGraphEditorFactory();
        var blockModelFactory = new BlockModelFactory(blockDefLibrary, blockFuncLibrary);
        var saveDocument = new SaveDocument(graphRepo);
        var workspaceFactoryNew = new WorkspaceFactoryNew(graphRepo, graphEditorFactory, blockModelFactory, saveDocument);
        var editor = new Editor(workspaceFactoryNew);

        var path = new File("btsxml/aslist-v2.btsxml").toPath();
        var document = graphRepo.load(path);

        this.stage = stage;
        stage.setTitle("BlockSmith: Blocks to Script");

        // Initialize views
        var workspaceView = new WorkspaceView();
        var blockSearchView = new BlockSearchView();
        var selectionRectangleView = new SelectionRectangleView();
        var zoomView = new ZoomView();
        var radialMenuView = new RadialMenuView();
        var menuBarView = new MenuBarView();
        var editorView = new EditorView(radialMenuView, workspaceView, menuBarView, zoomView, selectionRectangleView, blockSearchView);

        // Create workspace
        var editorContext = new EditorContext(editorView);
        var commandFactory = new CommandFactory(editorContext, blockModelFactory, graphRepo);
        var actionManager = new ActionManager(editorContext, commandFactory);
        var workspaceFactory = new WorkspaceFactory(graphEditorFactory, actionManager, commandFactory, blockModelFactory, saveDocument);
        var workspaceContext = workspaceFactory.openDocument(path, document);

        var newWorkspace = workspaceContext.controller().getView();
        var index = editorView.getChildren().indexOf(workspaceView);

        editorView.getChildren().remove(workspaceView);
        editorView.getChildren().add(index, newWorkspace);

        // initialize EventRouter for Context
        EditorEventRouter eventRouter = new EditorEventRouter();

        // Initialize controllers
        var zoomController = new ZoomController(actionManager, commandFactory, eventRouter, editorContext, zoomView);
        zoomController.bindZoomLabel(workspaceContext.controller().getModel().zoomFactorProperty());
        var blockSearchController = new BlockSearchController(actionManager, commandFactory, eventRouter, editorContext, blockSearchView, blockDefLibrary);
        var selectionRectangleController = new SelectionRectangleController(actionManager, commandFactory, eventRouter, editorContext, selectionRectangleView);
        var panController = new PanController(eventRouter, editorContext);
        var radialMenuController = new RadialMenuController(actionManager, commandFactory, eventRouter, editorContext, radialMenuView);
        var menuBarController = new MenuBarController(actionManager, commandFactory, editorContext, menuBarView);
        var editorController = new EditorController(eventRouter, editorView);

        // initialize ActionManager for Context
        editorContext.addWorkspace(workspaceContext);

        // Setup scene
        Scene scene = new Scene(editorView, APP_WIDTH, APP_HEIGHT);
        stage.setScene(scene);
        stage.show();
        stage.setFullScreen(false);

//        GraphLoader.deserialize(new File("btsxml/method-block.btsxml"), workspaceModel);
//        GraphLoader.deserialize(new File("btsxml/aslist.btsxml"), workspaceContext.controller().getModel());
//        GraphLoader.deserialize(new File("btsxml/addition.btsxml"), workspaceModel);
//        GraphLoader.deserialize(new File("btsxml/file.btsxml"), workspaceModel);
//        GraphLoader.deserialize(new File("btsxml/string-to-text.btsxml"), workspaceModel);
        editorView.printMenuBarHeight();

        Config.setStylesheetToScene(scene);
        stage.setOnCloseRequest(event -> {
            System.out.println("Closing application...");
            System.exit(0);  // Force JVM shutdown, triggering the shutdown hook
        });

        var keyboardController = new KeyboardController(actionManager, commandFactory, editorContext);
        scene.setOnKeyPressed(event -> keyboardController.handleShortcutTriggered(event));

        if (Config.showHelpOnStartup()) {
            HelpDialog.show();
        }

    }

    public static Stage getStage() {
        return stage;
    }

}

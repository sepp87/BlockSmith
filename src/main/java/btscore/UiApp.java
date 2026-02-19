package btscore;

import blocksmith.App;
import blocksmith.ui.BlockModelFactory;
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
import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

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
        var graphRepo = app.getGraphRepo();

        var graphDoc = graphRepo.load(new File("btsxml/aslist-v2.btsxml").toPath());

        this.stage = stage;
        stage.setTitle("BlockSmith: Blocks to Script");

        // Initialize views
        var tabPane = new TabPane();
        tabPane.setFocusTraversable(false);
        tabPane.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            var header = tabPane.lookup(".tab-header-area");
            if (header != null) {
                header.setFocusTraversable(false);
            }
        });
        tabPane.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            Platform.runLater(() -> {
                tabPane.lookupAll(".tab").forEach(node
                        -> node.setFocusTraversable(false)
                );
            });
        });
        tabPane.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (isFocused) {
                var selected = tabPane.getSelectionModel().getSelectedItem();
                if (selected != null && selected.getContent() != null) {
                    selected.getContent().requestFocus();
                }
            }
        });
        var workspaceView = new WorkspaceView();
        BlockSearchView blockSearchView = new BlockSearchView();
        SelectionRectangleView selectionRectangleView = new SelectionRectangleView();
        ZoomView zoomView = new ZoomView();
        RadialMenuView radialMenuView = new RadialMenuView();
        MenuBarView menuBarView = new MenuBarView();
        EditorView editorView = new EditorView(radialMenuView, workspaceView, tabPane, menuBarView, zoomView, selectionRectangleView, blockSearchView);

        // Create workspace
        var graphEditorFactory = app.getGraphEditorFactory();
        var editorContext = new EditorContext(editorView);
        var commandFactory = new CommandFactory(editorContext, blockModelFactory, graphRepo);
        var actionManager = new ActionManager(editorContext, commandFactory);
        var workspaceFactory = new WorkspaceFactory(graphEditorFactory, actionManager, commandFactory, blockModelFactory);
        var workspaceContext = workspaceFactory.create(graphDoc.graph());

        var workspaceTab = new Tab("New file");
//        workspaceTab.setContent(workspaceContext.controller().getView());
        var newWorkspace = workspaceContext.controller().getView();
        var index = editorView.getChildren().indexOf(workspaceView);

        editorView.getChildren().remove(workspaceView);
        editorView.getChildren().add(index, newWorkspace);

        tabPane.getTabs().add(workspaceTab);

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

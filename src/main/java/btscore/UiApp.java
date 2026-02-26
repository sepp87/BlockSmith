package btscore;

import blocksmith.App;
import blocksmith.ui.BlockModelFactory;
import blocksmith.ui.workspace.SaveDocument;
import blocksmith.ui.workspace.WorkspaceFactoryNew;
import btscore.editor.EditorContext;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;

import btscore.editor.EditorEventRouter;
import btscore.editor.EditorController;
import btscore.editor.EditorView;
import btscore.editor.keyboard.KeyboardController;
import btscore.editor.menubar.MenuBarController;
import btscore.editor.menubar.MenuBarView;
import btscore.editor.navigation.PanController;
import btscore.editor.selection.SelectionRectangleController;
import btscore.editor.selection.SelectionRectangleView;
import btscore.editor.radialmenu.RadialMenuController;
import btscore.editor.radialmenu.RadialMenuView;
import btscore.editor.navigation.ZoomController;
import btscore.editor.navigation.ZoomView;
import btscore.editor.blocksearch.BlockSearchController;
import btscore.editor.blocksearch.BlockSearchView;
import btscore.command.CommandDispatcher;
import btscore.command.CommandFactory;
import btscore.editor.tab.TabContent;
import btscore.editor.tab.TabManagerView;
import btscore.workspace.WorkspaceContext;
import btscore.workspace.WorkspaceController;
import btscore.workspace.WorkspaceFactory;
import btscore.workspace.WorkspaceState;
import btscore.workspace.WorkspaceView;

/**
 *
 * @author joostmeulenkamp
 */
public class UiApp extends Application {

    public static final boolean USE_TAB_MANAGER = true;
    private static App app;

    public static void setApp(App app) {
        UiApp.app = app;
    }

    public static final boolean LOG_FOCUS_OWNER = false;
    public static final boolean LOG_FREE_SPACE_CHECK = false;
    public static final boolean LOG_POTENTIAL_BUGS = true;
    public static final boolean LOG_METHOD_CALLS = false;
    public static final boolean LOG_EDITOR_STATE = true;

    public static final boolean TYPE_SENSITIVE = true;
    public static final boolean CONNECTION_REFACTOR = false;

    private static final double APP_WIDTH = 800;
    private static final double APP_HEIGHT = 800;
    private static Stage stage;

    @Override
    public void start(Stage stage) throws Exception {

        var path = new File("btsxml/aslist-v2.btsxml").toPath();

        var blockDefLibrary = app.getBlockDefLibrary();
        var blockFuncLibrary = app.getBlockFuncLibrary();
        var graphRepo = app.getGraphRepo();
        var graphEditorFactory = app.getGraphEditorFactory();
        var blockModelFactory = new BlockModelFactory(blockDefLibrary, blockFuncLibrary);
        var saveDocument = new SaveDocument(graphRepo);

        var document = graphRepo.load(path);

        this.stage = stage;
        stage.setTitle("BlockSmith: Blocks to Script");

        // Initialize views
        var blockSearchView = new BlockSearchView();
        var selectionRectangleView = new SelectionRectangleView();
        var zoomView = new ZoomView();
        var radialMenuView = new RadialMenuView();
        var menuBarView = new MenuBarView();
        var tabManagerView = new TabManagerView();

        if (USE_TAB_MANAGER) {

            var editorView = new EditorView(
                    tabManagerView,
                    radialMenuView,
                    null,
                    menuBarView,
                    zoomView,
                    selectionRectangleView,
                    blockSearchView
            );
            
            
            var wfn = new WorkspaceFactoryNew(graphRepo, graphEditorFactory, blockModelFactory, saveDocument);

            var editorContext = new EditorContext(editorView);
            var commandFactory = new CommandFactory(editorContext, blockModelFactory, graphRepo);
            var actionManager = new CommandDispatcher(editorContext, commandFactory);
            var workspaceFactory = new WorkspaceFactory(graphEditorFactory, actionManager, commandFactory, blockModelFactory, saveDocument);
            var workspaceContext = workspaceFactory.openDocument(path, document);
            
            // action manager needs to editor context to resolve current workspace
            //
            
            var wm = wfn.openDocument(path);
            var ws = new WorkspaceState();
            var wv = new WorkspaceView();
            var wConte = new WorkspaceContext(ws);
            var wContr = new WorkspaceController(actionManager, commandFactory, wConte, wm, wv);
            
            // initialize EventRouter for Context
            EditorEventRouter eventRouter = new EditorEventRouter();

            // Initialize controllers
            var zoomController = new ZoomController(actionManager, commandFactory, eventRouter, editorContext, zoomView);
            var blockSearchController = new BlockSearchController(actionManager, commandFactory, eventRouter, editorContext, blockSearchView, blockDefLibrary);
            var selectionRectangleController = new SelectionRectangleController(actionManager, commandFactory, eventRouter, editorContext, selectionRectangleView);
            var panController = new PanController(eventRouter, editorContext);
            var radialMenuController = new RadialMenuController(actionManager, commandFactory, eventRouter, editorContext, radialMenuView);
            var menuBarController = new MenuBarController(actionManager, editorContext, menuBarView);
            var editorController = new EditorController(eventRouter, editorView);

            // initialize ActionManager for Context
            editorContext.setOnActiveWorkspaceChanged(wContext -> {
                zoomController.bindZoomLabel(wContext.model().zoomFactorProperty());
                var id = wContext.id();
                var docPath = wContext.model().documentPath().orElse(null);
                var label = docPath == null ? null : docPath.getFileName().toString();
                var view = wContext.view();
                var tabContent = new TabContent(id, label, view);
                tabManagerView.addTab(tabContent);
            });

            editorContext.addWorkspace(workspaceContext);

            // Setup scene
            Scene scene = new Scene(editorView, APP_WIDTH, APP_HEIGHT);
            stage.setScene(scene);
            stage.show();
            stage.setFullScreen(false);

            editorView.printMenuBarHeight();
            editorView.requestFocus();

            Config.setStylesheetToScene(scene);
            stage.setOnCloseRequest(event -> {
                System.out.println("Closing application...");
                System.exit(0);  // Force JVM shutdown, triggering the shutdown hook
            });

            var keyboardController = new KeyboardController(actionManager, commandFactory);
            scene.setOnKeyPressed(event -> keyboardController.handleShortcutTriggered(event));

            if (Config.showHelpOnStartup()) {
                HelpDialog.show();
            }
        } else {

            // Initialize views
            var workspaceView = new WorkspaceView();
            var editorView = new EditorView(
                    tabManagerView,
                    radialMenuView,
                    workspaceView,
                    menuBarView,
                    zoomView,
                    selectionRectangleView,
                    blockSearchView
            );

            var editorContext = new EditorContext(editorView);
            var commandFactory = new CommandFactory(editorContext, blockModelFactory, graphRepo);
            var actionManager = new CommandDispatcher(editorContext, commandFactory);
            var workspaceFactory = new WorkspaceFactory(graphEditorFactory, actionManager, commandFactory, blockModelFactory, saveDocument);
            var workspaceContext = workspaceFactory.openDocument(path, document);

            var newWorkspace = workspaceContext.controller().getView();
            var index = editorView.getChildren().indexOf(workspaceView);
//
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
            var menuBarController = new MenuBarController(actionManager, editorContext, menuBarView);
            var editorController = new EditorController(eventRouter, editorView);

            editorContext.addWorkspace(workspaceContext);

            // Setup scene
            Scene scene = new Scene(editorView, APP_WIDTH, APP_HEIGHT);
            stage.setScene(scene);
            stage.show();
            stage.setFullScreen(false);

            editorView.printMenuBarHeight();

            Config.setStylesheetToScene(scene);
            stage.setOnCloseRequest(event -> {
                System.out.println("Closing application...");
                System.exit(0);  // Force JVM shutdown, triggering the shutdown hook
            });

            var keyboardController = new KeyboardController(actionManager, commandFactory);
            scene.setOnKeyPressed(event -> keyboardController.handleShortcutTriggered(event));

            if (Config.showHelpOnStartup()) {
                HelpDialog.show();
            }
        }

    }

    public static Stage getStage() {
        return stage;
    }

}

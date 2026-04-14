package blocksmith.ui;

import blocksmith.ui.help.HelpDialog;
import blocksmith.App;
import blocksmith.Environment;
import blocksmith.ui.graph.block.BlockModelFactory;
import blocksmith.app.workspace.SaveDocument;
import blocksmith.app.workspace.WorkspaceLifecycle;
import blocksmith.app.workspace.WorkspaceSessionFactory;
import blocksmith.ui.workspace.WorkspaceFxRegistry;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import blocksmith.ui.editor.EditorEventRouter;
import blocksmith.ui.editor.EditorController;
import blocksmith.ui.editor.EditorView;
import blocksmith.ui.editor.keyboard.KeyboardController;
import blocksmith.ui.editor.menubar.MenuBarController;
import blocksmith.ui.editor.menubar.MenuBarView;
import blocksmith.ui.editor.navigation.PanController;
import blocksmith.ui.editor.selection.SelectionRectangleController;
import blocksmith.ui.editor.selection.SelectionRectangleView;
import blocksmith.ui.editor.radialmenu.RadialMenuController;
import blocksmith.ui.editor.radialmenu.RadialMenuView;
import blocksmith.ui.editor.navigation.ZoomController;
import blocksmith.ui.editor.navigation.ZoomMenuView;
import blocksmith.ui.editor.blocksearch.BlockSearchController;
import blocksmith.ui.editor.blocksearch.BlockSearchView;
import blocksmith.app.command.CommandDispatcher;
import blocksmith.app.command.CoreCommands;
import blocksmith.app.outbound.WorkspaceHandle;
import blocksmith.ui.command.UiCommands;
import blocksmith.ui.editor.tab.TabContent;
import blocksmith.ui.editor.tab.TabManagerView;
import blocksmith.ui.workspace.WorkspaceFxFactory;
import java.nio.file.Path;

/**
 *
 * @author joostmeulenkamp
 */
public class UiApp extends Application {

    private static Environment env;
    private static Stage stage;

    private static final String RESOURCES_DIRECTORY = "src/main/resources/";
    private static final double APP_WIDTH = 800;
    private static final double APP_HEIGHT = 800;

    private App app;
    private StylesheetService stylesheetService;
    private UserPrefsService userPrefsService;

    public static final boolean LOG_FOCUS_OWNER = false;
    public static final boolean LOG_FREE_SPACE_CHECK = false;
    public static final boolean LOG_POTENTIAL_BUGS = true;
    public static final boolean LOG_METHOD_CALLS = false;
    public static final boolean LOG_EDITOR_STATE = false;

    public static final boolean TYPE_SENSITIVE = true;

    @Override
    public void start(Stage stage) throws Exception {

        Path path = null;
        var args = this.getParameters().getRaw();
        if (!args.isEmpty()) {
            path = Path.of(args.get(0));
        }

        var scheduler = new UiAppScheduler();
        this.app = new App(env.paths().getLibDir(), scheduler);

        this.stylesheetService
                = env.isDev()
                ? StylesheetService.forDev(RESOURCES_DIRECTORY)
                : StylesheetService.forProd();

        this.userPrefsService
                = env.isDev()
                ? UserPrefsService.forDev(path)
                : UserPrefsService.forProd();

        this.stage = stage;
        stage.setTitle("BlockSmith: Blocks to Script");

        // block factories
        var blockLibrary = app.getBlockLibrary();
        var blockModelFactory = new BlockModelFactory(blockLibrary);

        // core (graph, execution, workspace) 
        var graphRepo = app.getGraphRepo();
        var graphEditorFactory = app.getGraphEditorFactory();
        var executionSessionFactory = app.getExecutionSessionFactory();
        var saveDocument = new SaveDocument(graphRepo);
        var workspaceSessionFactory
                = new WorkspaceSessionFactory(
                        graphRepo,
                        graphEditorFactory,
                        executionSessionFactory,
                        saveDocument);

        // views
        var blockSearchView = new BlockSearchView();
        var selectionRectangleView = new SelectionRectangleView();
        var zoomView = new ZoomMenuView();
        var radialMenuView = new RadialMenuView();
        var menuBarView = new MenuBarView();
        var tabManagerView = new TabManagerView();

        var editorView = new EditorView(
                tabManagerView,
                radialMenuView,
                null,
                menuBarView,
                zoomView,
                selectionRectangleView,
                blockSearchView
        );

        // workspace
        var workspaceRegistry = new WorkspaceFxRegistry(editorView);
        var workspaceFactory = new WorkspaceFxFactory(workspaceSessionFactory, blockModelFactory);
        var workspaceLifecycle = new WorkspaceLifecycle(workspaceFactory, workspaceRegistry);
        WorkspaceHandle workspaceContext = null;
        var initialDocument = userPrefsService.getInitialDocument();
        if (initialDocument.isPresent()) {
            workspaceContext = workspaceFactory.openDocument(initialDocument.get());
        } else {
            workspaceContext = workspaceFactory.newDocument();
        }

        // commands
        var commandRegistry = app.getCommandRegistry();
        var commandDispatcher = new CommandDispatcher(workspaceRegistry, commandRegistry);
        var coreCommands = new CoreCommands(workspaceLifecycle, workspaceRegistry, blockLibrary);
        var uiCommands = new UiCommands(workspaceLifecycle, workspaceRegistry, userPrefsService);
        coreCommands.registerTo(commandRegistry);
        uiCommands.registerTo(commandRegistry);

        // editor event router
        EditorEventRouter eventRouter = new EditorEventRouter();

        // controllers
        var zoomController = new ZoomController(commandDispatcher, eventRouter, workspaceRegistry, zoomView);
        var blockSearchController = new BlockSearchController(commandDispatcher, eventRouter, workspaceRegistry, blockSearchView, blockLibrary);
        var selectionRectangleController = new SelectionRectangleController(commandDispatcher, eventRouter, workspaceRegistry, selectionRectangleView);
        var panController = new PanController(eventRouter, workspaceRegistry);
        var radialMenuController = new RadialMenuController(commandDispatcher, eventRouter, workspaceRegistry, radialMenuView);
        var menuBarController = new MenuBarController(commandDispatcher, workspaceRegistry, menuBarView, stylesheetService);
        var editorController = new EditorController(eventRouter, editorView);

        // active workspace listener
        workspaceRegistry.setOnActiveWorkspaceChanged(activeWorkspaceContext -> {
            tabManagerView.closeAll();
            zoomController.bindZoomLabel(activeWorkspaceContext.viewport().zoomFactorProperty());
            var id = activeWorkspaceContext.id();
            var docPath = activeWorkspaceContext.session().documentPath().orElse(null);
            var label = docPath == null ? null : docPath.getFileName().toString();
            var view = activeWorkspaceContext.view();
            var tabContent = new TabContent(id, label, view);
            tabManagerView.addTab(tabContent);
        });

        workspaceRegistry.add(workspaceContext);

        // Setup scene
        Scene scene = new Scene(editorView, APP_WIDTH, APP_HEIGHT);
        scene.getStylesheets().add(stylesheetService.getCss());

        stylesheetService.setOnCssUpdated(css -> {
            scene.getStylesheets().clear();
            scene.getStylesheets().add(css);
        });
        stage.setScene(scene);
        stage.show();
        stage.setFullScreen(false);

        editorView.printMenuBarHeight();
        editorView.requestFocus();

        stage.setOnCloseRequest(event -> {
            System.out.println("Closing application...");
            System.exit(0);  // Force JVM shutdown, triggering the shutdown hook
        });

        var keyboardController = new KeyboardController(commandDispatcher);
        scene.setOnKeyPressed(event -> keyboardController.handleShortcutTriggered(event));

        if (userPrefsService.showHelpOnStart()) {
            HelpDialog.show(stage, userPrefsService);
        }

    }

    public static void setEnv(Environment env) {
        UiApp.env = env;
    }
    
    public static Stage getStage() {
        return stage;
    }

}

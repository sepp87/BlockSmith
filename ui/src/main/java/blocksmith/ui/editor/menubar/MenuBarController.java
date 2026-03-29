package blocksmith.ui.editor.menubar;

import blocksmith.ui.workspace.WorkspaceFxRegistry;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import blocksmith.app.command.CommandDispatcher;
import blocksmith.app.command.Command;
import blocksmith.ui.PredefinedStyle;
import blocksmith.ui.StylesheetService;

/**
 *
 * @author joostmeulenkamp
 */
public class MenuBarController {

    private final CommandDispatcher actionManager;
    private final WorkspaceFxRegistry context;

    private final MenuBarView view;
    private final StylesheetService styles;

    public MenuBarController(CommandDispatcher actionManager, WorkspaceFxRegistry context, MenuBarView menuBarView, StylesheetService stylesheetService) {
        this.actionManager = actionManager;

        this.context = context;
        this.view = menuBarView;
        this.styles = stylesheetService;

        for (MenuItem item : view.getAllMenuItems()) {
            item.setOnAction(menuBarItemClickedHandler);
        }

        view.getFileMenu().showingProperty().addListener(fileMenuShownListener);
        view.getEditMenu().showingProperty().addListener(editMenuShownListener);
        view.getUndoMenuItem().setOnAction((e) -> undo());
        view.getRedoMenuItem().setOnAction((e) -> redo());

        for (MenuItem styleItem : view.getStyleMenuItems()) {
            styleItem.setOnAction((e) -> styles.setStyle(PredefinedStyle.valueOf(styleItem.getId())));
        }
    }

    private void undo() {
        actionManager.undo();
    }

    private void redo() {
        actionManager.redo();
    }

    private final ChangeListener<Boolean> fileMenuShownListener = this::onFileMenuShown;

    private void onFileMenuShown(Object b, Boolean o, Boolean n) {
        var workspace = context.active();
        var model = workspace.session();
        view.getSaveMenuItem().setDisable(model.isSaved());
    }

    private final ChangeListener<Boolean> editMenuShownListener = this::onEditMenuShown;

    private void onEditMenuShown(Object b, Boolean o, Boolean n) {
        var workspace = context.active();

        view.getUndoMenuItem().setDisable(!workspace.session().graphEditor().hasUndoableState());
        view.getRedoMenuItem().setDisable(!workspace.session().graphEditor().hasRedoableState());

        boolean isGroupable = workspace.session().isSelectionGroupable();
        view.getGroupMenuItem().disableProperty().set(!isGroupable);
    }

    private final EventHandler<ActionEvent> menuBarItemClickedHandler = this::handleMenuBarItemClicked;

    private void handleMenuBarItemClicked(ActionEvent event) {
        if (event.getSource() instanceof MenuItem menuItem) {
            actionManager.execute(Command.Id.valueOf(menuItem.getId()));
        }
    }

}

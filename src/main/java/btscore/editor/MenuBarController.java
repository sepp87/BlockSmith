package btscore.editor;

import btscore.editor.context.EditorContext;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import btscore.Config;
import btscore.editor.context.Command;

/**
 *
 * @author joostmeulenkamp
 */
public class MenuBarController {

    private final EditorContext context;
    private final MenuBarView view;

    public MenuBarController(EditorContext context, MenuBarView menuBarView) {
        this.context = context;
        this.view = menuBarView;

        for (MenuItem item : view.getAllMenuItems()) {
            item.setOnAction(menuBarItemClickedHandler);
        }

        view.getFileMenu().showingProperty().addListener(fileMenuShownListener);
        view.getEditMenu().showingProperty().addListener(editMenuShownListener);
        view.getUndoMenuItem().setOnAction((e) -> undo());
        view.getRedoMenuItem().setOnAction((e) -> redo());

        for (MenuItem styleItem : view.getStyleMenuItems()) {
            styleItem.setOnAction((e) -> Config.setStylesheet(view.getScene(), styleItem.getText()));
        }
    }

    private void undo() {
        context.activeWorkspace().actionManager().undo();
    }

    private void redo() {
        context.activeWorkspace().actionManager().redo();
    }

    private final ChangeListener<Boolean> fileMenuShownListener = this::onFileMenuShown;

    private void onFileMenuShown(Object b, Boolean o, Boolean n) {
        var workspace = context.activeWorkspace();
        var state = workspace.state();
        view.getSaveMenuItem().setDisable(!state.isSavable());
    }

    private final ChangeListener<Boolean> editMenuShownListener = this::onEditMenuShown;

    private void onEditMenuShown(Object b, Boolean o, Boolean n) {
        var workspace = context.activeWorkspace();
        var session = workspace.session();
        var controller = workspace.controller();

        view.getUndoMenuItem().setDisable(!session.hasUndoableState());
        view.getRedoMenuItem().setDisable(!session.hasRedoableState());

        boolean isGroupable = controller.areSelectedBlocksGroupable();
        view.getGroupMenuItem().disableProperty().set(!isGroupable);
    }

    private final EventHandler<ActionEvent> menuBarItemClickedHandler = this::handleMenuBarItemClicked;

    private void handleMenuBarItemClicked(ActionEvent event) {
        if (event.getSource() instanceof MenuItem menuItem) {
            context.activeWorkspace().actionManager().executeCommand(Command.Id.valueOf(menuItem.getId()));
        }
    }

}

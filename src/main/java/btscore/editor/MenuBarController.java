package btscore.editor;

import btscore.editor.context.EditorContext;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import btscore.Config;
import btscore.editor.context.ActionManager;
import btscore.editor.context.Command;
import btscore.editor.context.CommandFactory;

/**
 *
 * @author joostmeulenkamp
 */
public class MenuBarController {

    private final ActionManager actionManager;
    private final CommandFactory commandFactory;
    private final EditorContext context;

    private final MenuBarView view;

    public MenuBarController(ActionManager actionManager, CommandFactory commandFactory, EditorContext context, MenuBarView menuBarView) {
        this.actionManager = actionManager;
        this.commandFactory = commandFactory;

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
        actionManager.undo();
    }

    private void redo() {
        actionManager.redo();
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
        var controller = workspace.controller();

        view.getUndoMenuItem().setDisable(!workspace.controller().getModel().hasUndoableState());
        view.getRedoMenuItem().setDisable(!workspace.controller().getModel().hasRedoableState());

        boolean isGroupable = controller.areSelectedBlocksGroupable();
        view.getGroupMenuItem().disableProperty().set(!isGroupable);
    }

    private final EventHandler<ActionEvent> menuBarItemClickedHandler = this::handleMenuBarItemClicked;

    private void handleMenuBarItemClicked(ActionEvent event) {
        if (event.getSource() instanceof MenuItem menuItem) {
            actionManager.executeCommand(Command.Id.valueOf(menuItem.getId()));
        }
    }

}

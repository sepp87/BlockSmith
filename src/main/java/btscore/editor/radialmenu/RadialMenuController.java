package btscore.editor.radialmenu;

import btscore.editor.context.ActionManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.input.MouseEvent;
import btscore.utils.NodeHierarchyUtils;
import btscore.editor.context.EditorEventRouter;
import btscore.editor.context.Command;
import btscore.editor.context.CommandFactory;
import btscore.editor.context.EditorContext;
import static btscore.utils.EditorUtils.onFreeSpace;
import static btscore.utils.EventUtils.isRightClick;

/**
 *
 * @author joostmeulenkamp
 */
public class RadialMenuController {

    private final ActionManager actionManager;
    private final CommandFactory commandFactory;
    private final EditorEventRouter eventRouter;
    private final EditorContext context;
    private final RadialMenuView view;

    private final ChangeListener<Boolean> visibilityToggledHandler;

    public RadialMenuController(ActionManager actionManager, CommandFactory commandFactory, EditorEventRouter eventRouter, EditorContext context, RadialMenuView radialMenuView) {
        this.actionManager = actionManager;
        this.commandFactory = commandFactory;
        this.eventRouter = eventRouter;
        this.context = context;
        this.view = radialMenuView;

        for (RadialMenuItem item : view.getAllRadialMenuItems()) {
            item.setOnMouseClicked(this::handleRadialMenuItemClicked);
        }

        this.visibilityToggledHandler = this::handleToggleMouseMode;
        view.getRadialMenu().visibleProperty().addListener(visibilityToggledHandler);
        eventRouter.addEventListener(MouseEvent.MOUSE_CLICKED, this::toggleRadialMenu);
    }

    private void toggleRadialMenu(MouseEvent event) {
        var workspace = context.activeWorkspace();
        if (isRightClick(event) && onFreeSpace(event) && (workspace.state().isIdle() || view.getRadialMenu().isVisible())) {
            showView(event.getSceneX(), event.getSceneY());

        } else if (!NodeHierarchyUtils.isPickedNodeOrParentOfType(event, RadialMenu.class)) {
            // hide radial menu if any kind of click was anywhere else than on the menu
            hideView();
        }
    }

    private void handleRadialMenuItemClicked(MouseEvent event) {
        if (event.getSource() instanceof RadialMenuItem menuItem) {
            actionManager.executeCommand(Command.Id.valueOf(menuItem.getId()));
        }
        hideView();
    }

    private void handleToggleMouseMode(ObservableValue<? extends Boolean> observableValue, Boolean oldBoolean, Boolean isVisble) {
        var workspace = context.activeWorkspace();
        if (isVisble) {
            workspace.state().setAwaitingRadialMenu();
        } else {
            workspace.state().setIdle();
        }
    }

    private void showView(double x, double y) {
        view.getRadialMenu().show(x, y);
    }

    private void hideView() {
        view.getRadialMenu().setVisible(false);
    }
}

package blocksmith.ui.editor.radialmenu;

import blocksmith.app.command.CommandDispatcher;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.input.MouseEvent;
import blocksmith.ui.utils.NodeHierarchyUtils;
import blocksmith.ui.editor.EditorEventRouter;
import blocksmith.app.command.Command;
import blocksmith.ui.workspace.WorkspaceFxRegistry;
import static blocksmith.ui.utils.EditorUtils.onFreeSpace;
import static blocksmith.ui.utils.EventUtils.isRightClick;

/**
 *
 * @author joostmeulenkamp
 */
public class RadialMenuController {

    private final CommandDispatcher commandDispatcher;
    private final EditorEventRouter eventRouter;
    private final WorkspaceFxRegistry context;
    private final RadialMenuView view;

    private final ChangeListener<Boolean> visibilityToggledHandler;

    public RadialMenuController(
            CommandDispatcher commandDispatcher,
            EditorEventRouter eventRouter,
            WorkspaceFxRegistry context,
            RadialMenuView radialMenuView) {

        this.commandDispatcher = commandDispatcher;
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
        var workspace = context.active();
        if (isRightClick(event) && onFreeSpace(event) && (workspace.state().isIdle() || view.getRadialMenu().isVisible())) {
            showView(event.getSceneX(), event.getSceneY());

        } else if (!NodeHierarchyUtils.isPickedNodeOrParentOfType(event, RadialMenu.class)) {
            // hide radial menu if any kind of click was anywhere else than on the menu
            hideView();
        }
    }

    private void handleRadialMenuItemClicked(MouseEvent event) {
        if (event.getSource() instanceof RadialMenuItem menuItem) {
            commandDispatcher.execute(Command.Id.valueOf(menuItem.getId()));
        }
        hideView();
    }

    private void handleToggleMouseMode(ObservableValue<? extends Boolean> observableValue, Boolean oldBoolean, Boolean isVisble) {
        var workspace = context.active();
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

package btscore.editor;

import blocksmith.app.BlockDefLibrary;
import btscore.Launcher;
import javafx.beans.value.ChangeListener;
import static javafx.collections.FXCollections.observableArrayList;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import btscore.UiApp;
import btscore.graph.block.BlockLibraryLoader;
import btscore.utils.ListViewUtils;
import btscore.utils.NodeHierarchyUtils;
import btscore.editor.context.ActionManager;
import btscore.editor.context.EventRouter;
import btscore.editor.context.StateManager;
import btscore.editor.commands.CreateBlockCommand;
import static btscore.utils.EditorUtils.onFreeSpace;
import static btscore.utils.EventUtils.isDoubleClick;
import btscore.utils.ListViewHoverSelectBehaviour;
import btscore.workspace.WorkspaceController;
import javafx.collections.FXCollections;

/**
 *
 * @author Joost
 */
public class BlockSearchController extends BaseController {

    private static final double OFFSET = 20;
    private static final int ROWS_VISIBLE = 17;

    private final EventRouter eventRouter;
    private final ActionManager actionManager;
    private final StateManager state;
    private final BlockSearchView view;
    private final BlockDefLibrary blockDefLibrary;

    private Point2D creationPoint;

    private final TextField searchField;
    private final ListView<String> listView;

    private final ChangeListener<Boolean> searchFieldFocusChangedListener;

    public BlockSearchController(String contextId, BlockSearchView blockSearchView, BlockDefLibrary blockDefLibrary) {
        super(contextId);
        this.eventRouter = UiApp.getContext(contextId).getEventRouter();
        this.actionManager = UiApp.getContext(contextId).getActionManager();
        this.state = UiApp.getContext(contextId).getStateManager();

        this.view = blockSearchView;
        this.blockDefLibrary = blockDefLibrary;

        searchField = view.getSearchField();
        listView = view.getListView();

        searchField.setOnKeyPressed(this::handleShortcutAction);
        searchField.textProperty().addListener(this::handleSearchAction);
        searchFieldFocusChangedListener = this::handleRetainFocus;

        if (Launcher.BLOCK_DEF_LOADER) {
            listView.setItems(FXCollections.observableArrayList(blockDefLibrary.types()));
        } else {
            listView.setItems(BlockLibraryLoader.BLOCK_TYPE_LIST);
        }
        listView.setOnMouseClicked(this::handleCreateBlock);
        new ListViewHoverSelectBehaviour(listView);

        eventRouter.addEventListener(MouseEvent.MOUSE_CLICKED, this::toggleBlockSearch);
    }

    private void toggleBlockSearch(MouseEvent event) {
        if (isDoubleClick(event) && onFreeSpace(event) && state.isIdle()) {
            showView(event.getSceneX(), event.getSceneY());

        } else if (view.isVisible() && !NodeHierarchyUtils.isPickedNodeOrParentOfType(event, BlockSearchView.class)) {
            // hide block search if it is shown and the click was somewhere else 
            hideView();
        }
    }

    private void showView(double x, double y) {
        state.setAwaitingBlockSearch();
        creationPoint = new Point2D(x - OFFSET, y - OFFSET);
        view.setVisible(true);
        view.setTranslateX(x - OFFSET);
        view.setTranslateY(y - OFFSET);
        searchField.requestFocus();
        searchField.focusedProperty().addListener(searchFieldFocusChangedListener);
        listView.setPrefHeight(ListViewUtils.getCellHeight(listView) * ROWS_VISIBLE);
        listView.getSelectionModel().select(-1);
        listView.scrollTo(-1);
    }

    private void hideView() {
        view.setVisible(false);
        state.setIdle();
        searchField.setText("");
        searchField.focusedProperty().removeListener(searchFieldFocusChangedListener);
    }

    private void handleRetainFocus(Object b, boolean o, boolean isFocused) {
        if (!isFocused) {
            searchField.requestFocus();
        }
    }

    private void handleSearchAction(Object b, String o, String searchTerm) {
        searchTerm = searchTerm.toLowerCase();
        if (searchTerm.isBlank()) {
            if (Launcher.BLOCK_DEF_LOADER) {
                listView.setItems(FXCollections.observableArrayList(blockDefLibrary.types()));
            } else {
                listView.setItems(BlockLibraryLoader.BLOCK_TYPE_LIST);
            }
            listView.getSelectionModel().select(-1);
            return;
        }

        ObservableList<String> result = observableArrayList();

        if (Launcher.BLOCK_DEF_LOADER) {
            for (String type : blockDefLibrary.types()) {
                if (type.toLowerCase().contains(searchTerm)) {
                    result.add(type);
                }
            }
        } else {
            for (String type : BlockLibraryLoader.BLOCK_TYPE_LIST) {
                if (type.toLowerCase().contains(searchTerm)) {
                    result.add(type);
                }
            }
        }

        listView.setItems(result);
        listView.getSelectionModel().selectFirst();
    }

    private void handleShortcutAction(KeyEvent event) {
        switch (event.getCode()) {
            case DOWN, UP -> {
                int direction = event.getCode() == KeyCode.DOWN ? 1 : -1;
                ListViewUtils.scrollToWrapped(listView, direction, ROWS_VISIBLE);
            }
            case ESCAPE ->
                hideView();
            case ENTER ->
                createBlock();
        }
        event.consume(); // Consume the event so space does not trigger zoom to fit
    }

    private void handleCreateBlock(MouseEvent event) {
        createBlock();
    }

    private void createBlock() {
        String blockType = listView.getSelectionModel().getSelectedItem();
        if (blockType == null) {
            return;
        }

        System.out.println("Create block " + blockType);
        WorkspaceController workspaceController = actionManager.getWorkspaceController();
        Point2D location = workspaceController.getView().sceneToLocal(creationPoint);
        CreateBlockCommand createBlockCommand = new CreateBlockCommand(
                actionManager.getBlockModelFactory(),
                actionManager.getWorkspaceModel(),
                blockType,
                location);
        actionManager.executeCommand(createBlockCommand);

        hideView();
    }

}

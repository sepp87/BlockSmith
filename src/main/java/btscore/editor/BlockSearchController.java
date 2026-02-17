package btscore.editor;

import blocksmith.app.block.BlockDefLibrary;
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
import btscore.graph.block.BlockLibraryLoader;
import btscore.utils.ListViewUtils;
import btscore.utils.NodeHierarchyUtils;
import btscore.editor.context.ActionManager;
import btscore.editor.context.CommandFactory;
import btscore.editor.context.EditorContext;
import btscore.editor.context.EditorEventRouter;
import static btscore.utils.EditorUtils.onFreeSpace;
import static btscore.utils.EventUtils.isDoubleClick;
import btscore.utils.ListViewHoverSelectBehaviour;
import javafx.collections.FXCollections;

/**
 *
 * @author Joost
 */
public class BlockSearchController {

    private static final double OFFSET = 20;
    private static final int ROWS_VISIBLE = 17;

    private final ActionManager actionManager;
    private final CommandFactory commandFactory;
    private final EditorEventRouter eventRouter;
    private final EditorContext context;
    private final BlockSearchView view;
    private final BlockDefLibrary blockDefLibrary;

    private Point2D creationPoint;

    private final TextField searchField;
    private final ListView<String> listView;

    private final ChangeListener<Boolean> searchFieldFocusChangedListener;

    public BlockSearchController(ActionManager actionManager, CommandFactory commandFactory, EditorEventRouter eventRouter, EditorContext context, BlockSearchView blockSearchView, BlockDefLibrary blockDefLibrary) {
        this.actionManager = actionManager;
        this.commandFactory = commandFactory;

        this.eventRouter = eventRouter;
        this.context = context;

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
        var workspace = context.activeWorkspace();
        if (isDoubleClick(event) && onFreeSpace(event) && workspace.state().isIdle()) {
            showView(event.getSceneX(), event.getSceneY());

        } else if (view.isVisible() && !NodeHierarchyUtils.isPickedNodeOrParentOfType(event, BlockSearchView.class)) {
            // hide block search if it is shown and the click was somewhere else 
            hideView();
        }
    }

    private void showView(double x, double y) {
        var workspace = context.activeWorkspace();
        workspace.state().setAwaitingBlockSearch();
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
        var workspace = context.activeWorkspace();
        view.setVisible(false);
        workspace.state().setIdle();
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

//        System.out.println("Create block " + blockType);
        var location = context.sceneToWorkspace(creationPoint);
        var workspace = context.activeWorkspace();
        var command = commandFactory.createAddBlockCommand(blockType, location);
        actionManager.executeCommand(command);
        hideView();
    }

}

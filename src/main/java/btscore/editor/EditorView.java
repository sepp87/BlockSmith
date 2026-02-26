package btscore.editor;

import btscore.editor.selection.SelectionRectangleView;
import btscore.editor.blocksearch.BlockSearchView;
import btscore.editor.navigation.ZoomView;
import btscore.editor.menubar.MenuBarView;
import btscore.UiApp;
import btscore.editor.radialmenu.RadialMenuView;
import javafx.scene.layout.AnchorPane;
import btscore.editor.tab.TabManagerView;
import btscore.workspace.Background;
import btscore.workspace.WorkspaceView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 *
 * @author Joost
 */
public class EditorView extends BorderPane {

    MenuBarView menuBarView;

    public EditorView(
            TabManagerView tabManagerView,
            RadialMenuView radialMenuView,
            WorkspaceView workspaceView,
            MenuBarView menuBarView,
            ZoomView zoomView,
            SelectionRectangleView selectionRectangleView,
            BlockSearchView blockSearchView
    ) {

        this.getStyleClass().add("bts");

        this.menuBarView = menuBarView;

        AnchorPane.setTopAnchor(zoomView, 10.);
        AnchorPane.setRightAnchor(zoomView, 10.);

        setTop(menuBarView);

        var content = UiApp.USE_TAB_MANAGER ? tabManagerView : workspaceView;

        var background = new Background();
//        background.getStyleClass().add("debug");
        AnchorPane.setTopAnchor(background, 0.);
        AnchorPane.setRightAnchor(background, 0.);
        AnchorPane.setBottomAnchor(background, 0.);
        AnchorPane.setLeftAnchor(background, 0.);

        selectionRectangleView.setManaged(false);

        var overlay = new AnchorPane(
                background,
                content,
                selectionRectangleView,
                radialMenuView,
                blockSearchView,
                zoomView
        );

        setCenter(overlay);

    }

    public void printMenuBarHeight() {
        System.out.println("EditorView.printMenuBarHeight() " + menuBarView.getHeight());
    }

}

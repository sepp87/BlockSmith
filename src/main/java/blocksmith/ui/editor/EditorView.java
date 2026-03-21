package blocksmith.ui.editor;

import blocksmith.ui.editor.selection.SelectionRectangleView;
import blocksmith.ui.editor.blocksearch.BlockSearchView;
import blocksmith.ui.editor.navigation.ZoomView;
import blocksmith.ui.editor.menubar.MenuBarView;
import blocksmith.ui.UiApp;
import blocksmith.ui.editor.radialmenu.RadialMenuView;
import javafx.scene.layout.AnchorPane;
import blocksmith.ui.editor.tab.TabManagerView;
import blocksmith.ui.workspace.Background;
import blocksmith.ui.workspace.WorkspaceView;
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

        var content = tabManagerView;

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

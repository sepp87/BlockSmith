package btscore.editor;

import btscore.editor.radialmenu.RadialMenuView;
import javafx.scene.layout.AnchorPane;
import btscore.Config;
import btscore.workspace.WorkspaceView;
import javafx.scene.control.TabPane;

/**
 *
 * @author Joost
 */
public class EditorView extends AnchorPane {

    MenuBarView menuBarView;

    public EditorView(
            RadialMenuView radialMenuView,
                        WorkspaceView workspaceView,
            TabPane tabPane,
            MenuBarView menuBarView,
            ZoomView zoomView,
            SelectionRectangleView selectionRectangleView,
            BlockSearchView blockSearchView) {

//        this.getStylesheets().add(Config.get().stylesheets());
        this.getStyleClass().add("bts");

        this.menuBarView = menuBarView;

        menuBarView.prefWidthProperty().bind(this.widthProperty());

        AnchorPane.setTopAnchor(zoomView, 37.5);
        AnchorPane.setRightAnchor(zoomView, 10.);

        AnchorPane.setTopAnchor(tabPane, 10.);
        AnchorPane.setRightAnchor(tabPane, 0.);
        AnchorPane.setBottomAnchor(tabPane, 0.);
        AnchorPane.setLeftAnchor(tabPane, 0.);

        // create selection block
        this.getChildren().addAll(
                                workspaceView,
//                tabPane,
                radialMenuView,
                menuBarView,
                zoomView,
                selectionRectangleView,
                blockSearchView
        );

    }

    public void printMenuBarHeight() {
        System.out.println("EditorView.printMenuBarHeight() " + menuBarView.getHeight());
    }

}

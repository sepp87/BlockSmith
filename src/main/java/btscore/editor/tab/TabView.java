package btscore.editor.tab;

import java.util.function.Consumer;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

/**
 *
 * @author joost
 */
public class TabView extends HBox {


    private final Button labelButton;
    private final Button closeButton;

    private Consumer<String> contentRequestListener;
    private Consumer<String> closeRequestListener;

    public TabView(String id, String label) {
        setId(id);
        var root = new HBox();
        labelButton = new Button(label);
        labelButton.setOnAction(e -> onTabContentRequested(id));
        closeButton = new Button("X");
        closeButton.setOnAction(e -> onTabCloseRequested(id));
        root.getChildren().addAll(labelButton, closeButton);
    }


    public void renameTab(String label) {
        labelButton.setText(label);
    }

    public void setOnTabContentRequested(Consumer<String> listener) {
        contentRequestListener = listener;
    }

    private void onTabContentRequested(String id) {
        if (contentRequestListener != null) {
            contentRequestListener.accept(id);
        }
    }

    public void setOnTabCloseRequested(Consumer<String> listener) {
        closeRequestListener = listener;
    }

    private void onTabCloseRequested(String id) {
        if (closeRequestListener != null) {
            closeRequestListener.accept(id);
        }
    }

    public void dispose() {
        labelButton.setOnAction(null);
        closeButton.setOnAction(null);
    }
}

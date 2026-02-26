package btscore.editor.tab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 *
 * @author joost
 */
public class TabManagerView extends BorderPane {

    public static final String DEFAULT_TAB_LABEL = "New Workspace";
    private final HBox tabHeader;
    private final Pane contentRoot;
    private final Map<String, TabEntry> tabs = new HashMap<>();
    private final List<Consumer<String>> tabShownListeners = new ArrayList<>();
    private final List<Consumer<String>> tabClosedListeners = new ArrayList<>();
    private final List<String> tabOrder = new ArrayList<>();
    private String active;

    private static record TabEntry(
            TabView tab,
            Node content) {

    }

    public TabManagerView() {

        tabHeader = new HBox();
        contentRoot = new AnchorPane();
        setTop(tabHeader);
        tabHeader.getStyleClass().add("debug");
        setCenter(contentRoot);
//        
//                this.getStyleClass().add("debug");
//                                contentRoot.getStyleClass().add("debug-2");

                                this.setPickOnBounds(false);
                      contentRoot.setPickOnBounds(false);


    }

    public void addTab(TabContent content) {
        var id = content.id().toString();
        var label = labelOrDefault(content);
        var tab = new TabView(id, label);
        tab.setOnTabContentRequested(this::showTab);
        tab.setOnTabCloseRequested(this::closeTab);

        tabHeader.getChildren().add(tab);

        var view = content.view();
        view.setStyle("-fx-background-color: green;");
        view.setVisible(false);
        contentRoot.getChildren().add(view);

        tabOrder.add(id);
        var entry = new TabEntry(tab, view);
        tabs.put(id, entry);

        showTab(id);

    }

    private String labelOrDefault(TabContent content) {
        var label = content.label();
        return label == null || label.isBlank() ? DEFAULT_TAB_LABEL : label;
    }

    public void renameTab(String id, String label) {
        tabs.get(id).tab.renameTab(label);
    }

    public void showTab(String id) {

        if (active != null) {
            var previous = tabs.get(active);
            // TODO set css to inactive
            previous.content.setVisible(false);
        }

        var entry = tabs.get(id);
        // TODO set css to active
        entry.content.setVisible(true);

        active = id;
        onTabShown();
    }

    public void closeTab(String id) {
        var nextActiveId = resolveNextActiveFrom(id);
        nextActiveId.ifPresent(this::showTab);

        tabOrder.remove(id);
        var entry = tabs.remove(id);
        tabHeader.getChildren().remove(entry.tab);
        entry.tab.dispose();

        contentRoot.getChildren().remove(entry.content);
        onTabClosed(id);
    }

    private Optional<String> resolveNextActiveFrom(String id) {
        if (id.equals(active)) {
            var size = tabOrder.size();
            var index = tabOrder.indexOf(id);
            var next = index + 1;
            if (next < size) {
                return Optional.of(tabOrder.get(next));
            }
            var previous = index - 1;
            if (previous > -1) {
                return Optional.of(tabOrder.get(previous));
            }
        }
        return Optional.empty();
    }

    public void setOnTabShown(Consumer<String> listener) {
        tabShownListeners.add(listener);
    }

    public void setOnTabClosed(Consumer<String> listner) {
        tabClosedListeners.add(listner);
    }

    private void onTabShown() {
        tabShownListeners.forEach(c -> c.accept(active));
    }

    private void onTabClosed(String id) {
        tabClosedListeners.forEach(c -> c.accept(id));
    }

}

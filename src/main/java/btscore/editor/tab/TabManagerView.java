package btscore.editor.tab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import javafx.scene.Node;
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
    private final HBox tabsHeader;
    private final Pane contentRoot;
    private final Map<String, TabView> tabs = new HashMap<>();
    private final Map<String, Node> contents = new HashMap<>();
    private final List<Consumer<String>> tabShownListeners = new ArrayList<>();
    private final List<Consumer<String>> tabClosedListeners = new ArrayList<>();

    private String activeTabId;

    public TabManagerView() {

        tabsHeader = new HBox();
        contentRoot = new StackPane();
        setTop(tabsHeader);
        setCenter(contentRoot);

    }

    public void addTab(TabContent content) {
        var id = content.id().toString();
        var label = labelOrDefault(content);
        var tab = new TabView(id, label);
        tab.setOnTabContentRequested(this::showTab);
        tab.setOnTabCloseRequested(this::closeTab);

        tabs.put(id, tab);
        tabsHeader.getChildren().add(tab);

        var view = content.view();
        contents.put(id, view);
        contentRoot.getChildren().add(view);

        showTab(id);

    }

    private String labelOrDefault(TabContent content) {
        var label = content.label();
        return label == null || label.isBlank() ? label : DEFAULT_TAB_LABEL;
    }

    public void showTab(String id) {

        if (activeTabId != null) {
            var activeTab = tabs.get(activeTabId);
            // TODO set css to inactive
            var activeContent = contents.get(activeTabId);
            activeContent.setVisible(false);
        }

        var tab = tabs.get(id);
        // TODO set css to active
        var content = contents.get(id);
        content.setVisible(true);

        activeTabId = id;
        onTabShown();
    }

    public void closeTab(String id) {
        var nextActiveId = resolveNextActiveTabFrom(id);
        nextActiveId.ifPresent(this::showTab);

        var tab = tabs.get(id);
        tabsHeader.getChildren().remove(tab);
        tab.dispose();

        var content = contents.get(id);
        contentRoot.getChildren().remove(content);
        onTabClosed(id);
    }

    private Optional<String> resolveNextActiveTabFrom(String id) {
        if (id.equals(activeTabId)) {
            var tab = tabs.get(id);
            var index = tabsHeader.getChildren().indexOf(tab);
            var iterator = tabsHeader.getChildren().listIterator(index);
            if (iterator.hasNext()) {
                return Optional.of(iterator.next().getId());
            } else if (iterator.hasPrevious()) {
                return Optional.of(iterator.previous().getId());
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
        tabShownListeners.forEach(c -> c.accept(activeTabId));
    }

    private void onTabClosed(String id) {
        tabClosedListeners.forEach(c -> c.accept(activeTabId));
    }

}

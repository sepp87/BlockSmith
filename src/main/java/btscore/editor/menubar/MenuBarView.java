package btscore.editor.menubar;

import static btscore.command.Command.Id.*;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;

/**
 *
 * @author joostmeulenkamp
 */
public class MenuBarView extends MenuBar {

    private final Menu fileMenu;
    private final MenuItem save;

    private final Menu editMenu;
    private final MenuItem undo;
    private final MenuItem redo;
    private final MenuItem group;
    
    private final Menu styleMenu;

    public MenuBarView() {
        this.setUseSystemMenuBar(true);

        fileMenu = new Menu("File");
        MenuItem newFile = new MenuItem("New file", NEW_FILE.name());
        MenuItem openFile = new MenuItem("Open file", OPEN_FILE.name());
        save = new MenuItem("Save", SAVE_FILE.name());
        MenuItem saveAs = new MenuItem("Save as", SAVE_AS_FILE.name());
        fileMenu.getItems().addAll(newFile, openFile, save, saveAs);

        this.editMenu = new Menu("Edit");
        this.undo = new MenuItem("Undo", "UNDO");
        this.redo = new MenuItem("Redo", "REDO");
        MenuItem copy = new MenuItem("Copy", COPY_BLOCKS.name());
        MenuItem paste = new MenuItem("Paste", PASTE_BLOCKS.name());
        MenuItem delete = new MenuItem("Delete", REMOVE_BLOCKS.name());
        this.group = new MenuItem("Group", ADD_GROUP.name());
        Menu alignMenu = new Menu("Align");
        editMenu.getItems().addAll(undo, redo, copy, paste, delete, group, alignMenu);

        MenuItem alignLeft = new MenuItem("Align left", ALIGN_LEFT.name());
        MenuItem alignVertically = new MenuItem("Align vertically", ALIGN_VERTICALLY.name());
        MenuItem alignRight = new MenuItem("Align right", ALIGN_RIGHT.name());
        MenuItem alignTop = new MenuItem("Align top", ALIGN_TOP.name());
        MenuItem alignHorizontally = new MenuItem("Align horizontally", ALIGN_HORIZONTALLY.name());
        MenuItem alignBottom = new MenuItem("Align bottom", ALIGN_BOTTOM.name());
        alignMenu.getItems().addAll(alignLeft, alignVertically, alignRight, alignTop, alignHorizontally, alignBottom);

        Menu viewMenu = new Menu("View");
        MenuItem zoomToFit = new MenuItem("Zoom to fit", ZOOM_TO_FIT.name());
        MenuItem zoomIn = new MenuItem("Zoom in", ZOOM_IN.name());
        MenuItem zoomOut = new MenuItem("Zoom out", ZOOM_OUT.name());
        viewMenu.getItems().addAll(zoomToFit, zoomIn, zoomOut);

        Menu extrasMenu = new Menu("Extras");
        MenuItem reloadPlugins = new MenuItem("Reload plugins", RELOAD_PLUGINS.name());
        MenuItem logErrors = new MenuItem("Log errors", "LOG_ERRORS");
        MenuItem help = new MenuItem("Help", HELP.name());
        this.styleMenu = new Menu("Style");
        extrasMenu.getItems().addAll(reloadPlugins, logErrors, help, styleMenu);

        MenuItem light = new MenuItem("Light", "STYLESHEET");
        MenuItem dark = new MenuItem("Dark", "STYLESHEET");
        MenuItem singer = new MenuItem("Singer", "STYLESHEET");
        styleMenu.getItems().addAll(light, dark, singer);

        logErrors.setDisable(true);

        this.getMenus().addAll(fileMenu, editMenu, viewMenu, extrasMenu);
    }

    public Menu getFileMenu() {
        return fileMenu;
    }

    public MenuItem getSaveMenuItem() {
        return save;
    }

    public Menu getEditMenu() {
        return editMenu;
    }

    public MenuItem getUndoMenuItem() {
        return undo;
    }

    public MenuItem getRedoMenuItem() {
        return redo;
    }

    public MenuItem getGroupMenuItem() {
        return group;
    }
    
    public List<MenuItem> getStyleMenuItems(){
        return getMenuItemsFrom(styleMenu);
    }

    public class MenuItem extends javafx.scene.control.MenuItem {

        public MenuItem(String name, String id) {
            super(name);
            this.idProperty().set(id);
        }
    }

    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> result = new ArrayList<>();
        for (Menu menu : this.getMenus()) {
            result.addAll(getMenuItemsFrom(menu));
        }
        return result;
    }

    private List<MenuItem> getMenuItemsFrom(Menu menu) {
        List<MenuItem> result = new ArrayList<>();
        for (javafx.scene.control.MenuItem item : menu.getItems()) {
            if (item instanceof MenuItem menuItem) {
                result.add(menuItem);
            } else if (item instanceof Menu subMenu) {
                result.addAll(getMenuItemsFrom(subMenu));
            }
        }
        return result;
    }

}

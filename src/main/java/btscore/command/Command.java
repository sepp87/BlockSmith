package btscore.command;

import btscore.workspace.WorkspaceContext;

/**
 *
 * @author Joost
 */
public interface Command {

    boolean execute();

    
    public enum Id {
        NEW_FILE,
        OPEN_FILE,
        SAVE_FILE,
        SAVE_AS_FILE,
        COPY_BLOCKS,
        PASTE_BLOCKS,
        REMOVE_BLOCKS,
        SELECT_ALL_BLOCKS,
        DESELECT_ALL_BLOCKS,
        ADD_GROUP,
        ALIGN_TOP,
        ALIGN_BOTTOM,
        ALIGN_LEFT,
        ALIGN_RIGHT,
        ALIGN_VERTICALLY,
        ALIGN_HORIZONTALLY,
        ZOOM_TO_FIT,
        ZOOM_IN,
        ZOOM_OUT,
        RELOAD_PLUGINS,
        HELP
    }
}

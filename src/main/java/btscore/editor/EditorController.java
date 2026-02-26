package btscore.editor;

import blocksmith.ui.EditorModel;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/**
 *
 * @author Joost
 */
public class EditorController {

    private final EditorEventRouter eventRouter;

    private final EditorView view;

    public EditorController(EditorEventRouter eventRouter, EditorView editorView) {

        this.eventRouter = eventRouter;
        this.view = editorView;
        
        view.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseClickedHandler); // capture the event before the sub menu is removed from the radial menu when clicking on "Return To Main" from a sub menu 
        view.addEventFilter(MouseEvent.MOUSE_PRESSED, mousePressedHandler);
        view.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseDraggedHandler);
        view.addEventFilter(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);
        view.addEventHandler(ScrollEvent.SCROLL_STARTED, scrollStartedHandler);
        view.addEventHandler(ScrollEvent.SCROLL, scrollUpdatedHandler);
        view.addEventHandler(ScrollEvent.SCROLL_FINISHED, scrollFinishedHandler);
    }

    private final EventHandler<MouseEvent> mouseClickedHandler = this::handleMouseClicked;

    private void handleMouseClicked(MouseEvent event) {
        eventRouter.fireEvent(event);
    }

    private final EventHandler<MouseEvent> mousePressedHandler = this::handleMousePressed;

    private void handleMousePressed(MouseEvent event) {
        eventRouter.fireEvent(event);
    }

    private final EventHandler<MouseEvent> mouseDraggedHandler = this::handleMouseDragged;

    private void handleMouseDragged(MouseEvent event) {
        eventRouter.fireEvent(event);
    }

    private final EventHandler<MouseEvent> mouseReleasedHandler = this::handleMouseReleased;

    private void handleMouseReleased(MouseEvent event) {
        eventRouter.fireEvent(event);
    }

    private final EventHandler<ScrollEvent> scrollStartedHandler = this::handleScrollStarted;

    private void handleScrollStarted(ScrollEvent event) {
        eventRouter.fireEvent(event);
    }

    private final EventHandler<ScrollEvent> scrollUpdatedHandler = this::handleScroll;

    private void handleScroll(ScrollEvent event) {
        eventRouter.fireEvent(event);
    }

    private final EventHandler<ScrollEvent> scrollFinishedHandler = this::handleScrollFinished;

    private void handleScrollFinished(ScrollEvent event) {
        eventRouter.fireEvent(event);
    }

}

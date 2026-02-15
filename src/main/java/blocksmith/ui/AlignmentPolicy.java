package blocksmith.ui;

import blocksmith.app.block.MoveBlockRequest;
import blocksmith.domain.block.BlockId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.layout.Pane;

/**
 *
 * @author joost
 */
public class AlignmentPolicy {

    public enum Mode {
        TOP,
        BOTTOM,
        LEFT,
        RIGHT,
        HORIZONTALLY,
        VERTICALLY
    }

    public List<MoveBlockRequest> apply(Collection<? extends Pane> views, Mode mode) {

        if (views.isEmpty()) {
            return List.of();
        }

        var bounds = boundingBoxOf(views);

        var result = new ArrayList<MoveBlockRequest>();
        for (var view : views) {
            var id = BlockId.from(view.getId());
            var x = computeX(mode, bounds, view);
            var y = computeY(mode, bounds, view);
            var request = new MoveBlockRequest(id, x, y);
            result.add(request);
        }
        return result;
    }

    private Double computeX(Mode mode, Bounds bounds, Pane view) {
        return switch (mode) {
            case TOP ->
                view.getLayoutX();
            case BOTTOM ->
                view.getLayoutX();
            case LEFT ->
                bounds.getMinX();
            case RIGHT ->
                bounds.getMaxX() - view.getWidth();
            case VERTICALLY ->
                bounds.getMaxX() - bounds.getWidth() / 2 - view.getWidth() / 2;
            case HORIZONTALLY ->
                view.getLayoutX();
        };
    }

    private Double computeY(Mode mode, Bounds bounds, Pane view) {
        return switch (mode) {
            case TOP ->
                bounds.getMinY();
            case BOTTOM ->
                bounds.getMaxY() - view.getHeight();
            case LEFT ->
                view.getLayoutY();
            case RIGHT ->
                view.getLayoutY();
            case VERTICALLY ->
                view.getLayoutY();
            case HORIZONTALLY ->
                bounds.getMaxY() - bounds.getHeight() / 2 - view.getHeight() / 2;
        };
    }

    private static Bounds boundingBoxOf(Collection<? extends Pane> panes) {

        double minLeft = Double.MAX_VALUE;
        double minTop = Double.MAX_VALUE;
        double maxLeft = Double.MIN_VALUE;
        double maxTop = Double.MIN_VALUE;

        for (var pane : panes) {
            if (pane.getLayoutX() < minLeft) {
                minLeft = pane.getLayoutX();
            }
            if (pane.getLayoutY() < minTop) {
                minTop = pane.getLayoutY();
            }

            if ((pane.getLayoutX() + pane.getWidth()) > maxLeft) {
                maxLeft = pane.getLayoutX() + pane.getWidth();
            }
            if ((pane.getLayoutY() + pane.getHeight()) > maxTop) {
                maxTop = pane.getLayoutY() + pane.getHeight();
            }
        }
        return new BoundingBox(minLeft, minTop, maxLeft - minLeft, maxTop - minTop);

    }
}

//public enum Alignment {
//
//    TOP {
//        @Override double x(Bounds b, Pane v) { return v.getLayoutX(); }
//        @Override double y(Bounds b, Pane v) { return b.getMinY(); }
//    },
//    BOTTOM {
//        @Override double x(Bounds b, Pane v) { return v.getLayoutX(); }
//        @Override double y(Bounds b, Pane v) { return b.getMaxY() - v.getHeight(); }
//    },
//    LEFT {
//        @Override double x(Bounds b, Pane v) { return b.getMinX(); }
//        @Override double y(Bounds b, Pane v) { return v.getLayoutY(); }
//    },
//    RIGHT {
//        @Override double x(Bounds b, Pane v) { return b.getMaxX() - v.getWidth(); }
//        @Override double y(Bounds b, Pane v) { return v.getLayoutY(); }
//    },
//    HORIZONTAL_CENTER {
//        @Override double x(Bounds b, Pane v) { return v.getLayoutX(); }
//        @Override double y(Bounds b, Pane v) {
//            return b.getMinY() + b.getHeight() / 2 - v.getHeight() / 2;
//        }
//    },
//    VERTICAL_CENTER {
//        @Override double x(Bounds b, Pane v) {
//            return b.getMinX() + b.getWidth() / 2 - v.getWidth() / 2;
//        }
//        @Override double y(Bounds b, Pane v) { return v.getLayoutY(); }
//    };
//
//    abstract double x(Bounds bounds, Pane view);
//    abstract double y(Bounds bounds, Pane view);
//}
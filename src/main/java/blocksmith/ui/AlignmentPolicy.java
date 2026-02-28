package blocksmith.ui;

import blocksmith.domain.block.BlockPosition;
import blocksmith.domain.block.BlockId;
import btscore.graph.block.BlockModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;

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

    public List<BlockPosition> apply(Collection<BlockModel> blocks, Mode mode) {

        if (blocks.isEmpty()) {
            return List.of();
        }

        var allBounds = blocks.stream().map(BlockModel::getMeasuredBounds).toList();
        var bounds = boundsOf(allBounds);

        var result = new ArrayList<BlockPosition>();
        for (var block : blocks) {
            var id = BlockId.from(block.getId());
            var x = computeX(mode, bounds, block.getMeasuredBounds());
            var y = computeY(mode, bounds, block.getMeasuredBounds());
            var request = new BlockPosition(id, x, y);
            result.add(request);
        }
        return result;
    }

    private Double computeX(Mode mode, Bounds all, Bounds one) {
        return switch (mode) {
            case TOP ->
                one.getMinX();
            case BOTTOM ->
                one.getMinX();
            case LEFT ->
                all.getMinX();
            case RIGHT ->
                all.getMaxX() - one.getWidth();
            case VERTICALLY ->
                all.getMaxX() - all.getWidth() / 2 - one.getWidth() / 2;
            case HORIZONTALLY ->
                one.getMinX();
        };
    }

    private Double computeY(Mode mode, Bounds all, Bounds one) {
        return switch (mode) {
            case TOP ->
                all.getMinY();
            case BOTTOM ->
                all.getMaxY() - one.getHeight();
            case LEFT ->
                one.getMinY();
            case RIGHT ->
                one.getMinY();
            case VERTICALLY ->
                one.getMinY();
            case HORIZONTALLY ->
                all.getMaxY() - all.getHeight() / 2 - one.getHeight() / 2;
        };
    }

    private static Bounds boundsOf(Collection<Bounds> bounds) {

        double minX = bounds.stream().map(b -> b.getMinX()).reduce(Double.POSITIVE_INFINITY, Math::min);
        double minY = bounds.stream().map(b -> b.getMinY()).reduce(Double.POSITIVE_INFINITY, Math::min);
        double maxX = bounds.stream().map(b -> b.getMaxX()).reduce(Double.NEGATIVE_INFINITY, Math::max);
        double maxY = bounds.stream().map(b -> b.getMaxY()).reduce(Double.NEGATIVE_INFINITY, Math::max);

        return new BoundingBox(minX, minY, maxX - minX, maxY - minY);
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

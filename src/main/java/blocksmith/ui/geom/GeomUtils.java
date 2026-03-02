package blocksmith.ui.geom;

import java.util.Collection;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;

/**
 *
 * @author joost
 */
public final class GeomUtils {

    private GeomUtils() {

    }

    public static Bounds boundsOf(Collection<Bounds> bounds) {

        double minX = bounds.stream().map(b -> b.getMinX()).reduce(Double.POSITIVE_INFINITY, Math::min);
        double minY = bounds.stream().map(b -> b.getMinY()).reduce(Double.POSITIVE_INFINITY, Math::min);
        double maxX = bounds.stream().map(b -> b.getMaxX()).reduce(Double.NEGATIVE_INFINITY, Math::max);
        double maxY = bounds.stream().map(b -> b.getMaxY()).reduce(Double.NEGATIVE_INFINITY, Math::max);

        return new BoundingBox(minX, minY, maxX - minX, maxY - minY);
    }
}

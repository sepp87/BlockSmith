package blocksmith.domain.block;

/**
 *
 * @author joost
 */
public record BlockLayout(
        String label,
        double x,
        double y,
        double width,
        double height) {

    public static final double DEFAULT_POSITION = 0;
    public static final double DEFAULT_SIZE = -1;

    public static BlockLayout createEmpty() {
        return new BlockLayout(
                null,
                DEFAULT_POSITION,
                DEFAULT_POSITION,
                DEFAULT_SIZE,
                DEFAULT_SIZE
        );
    }

    public static BlockLayout create(Double x, Double y) {
        return new BlockLayout(
                null,
                x,
                y,
                DEFAULT_SIZE,
                DEFAULT_SIZE
        );
    }

    public static BlockLayout create(String label, Double x, Double y, Double width, Double height) {
        return new BlockLayout(
                label,
                x != null ? x : DEFAULT_POSITION,
                y != null ? y : DEFAULT_POSITION,
                width != null ? width : DEFAULT_SIZE,
                height != null ? height : DEFAULT_SIZE
        );
    }

    public BlockLayout withLabel(String label) {
        return new BlockLayout(label, x, y, width, height);
    }

    public BlockLayout withPosition(double x, double y) {
        return new BlockLayout(label, x, y, width, height);
    }

    public BlockLayout withSize(double width, double height) {
        return new BlockLayout(label, x, y, width, height);

    }

}

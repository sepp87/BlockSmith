package blocksmith.domain.block;

/**
 *
 * @author joost
 */
public record BlockLayout(
        String label,
        Double x,
        Double y,
        Double width,
        Double height) {

    public static BlockLayout createEmpty() {
        return new BlockLayout(null, null, null, null, null);
    }

    public static BlockLayout create(double x, double y) {
        return new BlockLayout(null, x, y, null, null);
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

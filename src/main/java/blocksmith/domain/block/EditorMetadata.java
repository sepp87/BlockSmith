package blocksmith.domain.block;

/**
 *
 * @author joost
 */
public record EditorMetadata (
        String label,
        double x,
        double y,
        Double width,
        Double height) {
    
    public static EditorMetadata create(double x, double y) {
        return new EditorMetadata(null, x, y, null, null);
    }

}

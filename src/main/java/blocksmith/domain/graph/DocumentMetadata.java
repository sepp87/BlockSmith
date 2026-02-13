package blocksmith.domain.graph;

import java.nio.file.Path;

/**
 *
 * @author joost
 */
public record DocumentMetadata(
        Path path,
        Double zoomFactor,
        Double translateX,
        Double translateY) {

    public static DocumentMetadata create(
            Path path,
            Double zoomFactor,
            Double translateX,
            Double translateY) {

        return new DocumentMetadata(path, zoomFactor, translateX, translateY);
    }

    public static DocumentMetadata createDefault() {
        return new DocumentMetadata(null, 1., 0., 0.);
    }
    
    public DocumentMetadata withPath(Path path) {
        return new DocumentMetadata(path, zoomFactor, translateX, translateY);
    }
}

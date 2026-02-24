package blocksmith.app;

import blocksmith.domain.graph.Graph;

/**
 *
 * @author joost
 */
public record GraphDocument(
        Graph graph,
        double zoomFactor,
        double translateX,
        double translateY) {

    public final static double DEFAULT_ZOOM_FACTOR = 1;
    public final static double DEFAULT_TRANSLATE = 0;

    public static GraphDocument createEmpty() {
        return new GraphDocument(
                Graph.createEmpty(),
                DEFAULT_ZOOM_FACTOR,
                DEFAULT_TRANSLATE,
                DEFAULT_TRANSLATE
        );
    }
}

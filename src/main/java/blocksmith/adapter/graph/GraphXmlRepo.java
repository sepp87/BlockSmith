package blocksmith.adapter.graph;

import blocksmith.domain.graph.DocumentMetadata;
import blocksmith.domain.graph.Graph;
import blocksmith.xml.v2.DocumentXml;
import blocksmith.xml.v2.ObjectFactory;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import java.nio.file.Path;

/**
 *
 * @author joost
 */
public class GraphXmlRepo {

    private final GraphXmlMapper mapper;
    private final Unmarshaller unmarshaller;
    private final Marshaller marshaller;

    public GraphXmlRepo(GraphXmlMapper mapper, JAXBContext context) throws JAXBException {
        this.mapper = mapper;
        this.unmarshaller = context.createUnmarshaller();
        this.marshaller = context.createMarshaller();
    }

    public Graph load(Path path) throws JAXBException {
        JAXBElement<?> jaxbElement = (JAXBElement) unmarshaller.unmarshal(path.toFile());
        var document = (DocumentXml) jaxbElement.getValue();
        var metadata = extractDocumentMetadata(path, document);
        return mapper.toDomain(document, metadata);
    }

    private DocumentMetadata extractDocumentMetadata(Path path, DocumentXml document) {
        return new DocumentMetadata(
                path,
                document.getZoomFactor(),
                document.getTranslateX(),
                document.getTranslateY()
        );
    }

    public void save(Path path, Graph graph) throws JAXBException {
        var jaxbElement = mapper.toXml(graph);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(jaxbElement, path.toFile());
    }

}

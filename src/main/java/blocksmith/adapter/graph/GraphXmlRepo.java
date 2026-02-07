package blocksmith.adapter.graph;

import blocksmith.domain.graph.Graph;
import blocksmith.xml.v2.DocumentXml;
import blocksmith.xml.v2.ObjectFactory;
import jakarta.xml.bind.JAXBContext;
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

    public GraphXmlRepo(GraphXmlMapper mapper) throws JAXBException {
        this.mapper = mapper;

        JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
        this.unmarshaller = context.createUnmarshaller();
        this.marshaller = context.createMarshaller();
    }

    public Graph load(Path path) throws JAXBException {
        var document = (DocumentXml) unmarshaller.unmarshal(path.toFile());
        return mapper.toDomain(path, document);
    }

    public void save(Graph graph) throws JAXBException {
        var path = graph.metadata().path();
        unmarshaller.unmarshal(path.toFile());
    }

}

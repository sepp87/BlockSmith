package blocksmith.infra.xml;

import blocksmith.app.GraphDocument;
import blocksmith.app.outbound.GraphRepo;
import blocksmith.xml.v2.DocumentXml;
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
public class GraphXmlRepo implements GraphRepo {

    private final GraphXmlMapper mapper;
    private final Unmarshaller unmarshaller;
    private final Marshaller marshaller;

    public GraphXmlRepo(GraphXmlMapper mapper, JAXBContext context) throws JAXBException {
        this.mapper = mapper;
        this.unmarshaller = context.createUnmarshaller();
        this.marshaller = context.createMarshaller();
    }

    @Override
    public GraphDocument load(Path path) throws JAXBException {
        JAXBElement<?> jaxbElement = (JAXBElement) unmarshaller.unmarshal(path.toFile());
        var document = (DocumentXml) jaxbElement.getValue();
        return mapper.toDomain(document);
    }


    @Override
    public void save(Path path, GraphDocument document) throws JAXBException {
        var jaxbElement = mapper.toXml(document);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(jaxbElement, path.toFile());
    }

}

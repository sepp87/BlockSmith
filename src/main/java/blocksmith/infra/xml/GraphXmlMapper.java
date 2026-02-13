package blocksmith.infra.xml;

import blocksmith.domain.block.BlockFactory;
import blocksmith.domain.graph.DocumentMetadata;
import blocksmith.domain.graph.Graph;
import blocksmith.xml.v2.DocumentXml;
import blocksmith.xml.v2.ObjectFactory;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import java.nio.file.Path;

/**
 *
 * @author joost
 */
public class GraphXmlMapper {


    private final ObjectFactory xmlFactory;
    private final BlockXmlMapper blockMapper;
    private final ConnectionXmlMapper connectionMapper;
    private final ValueXmlMapper valueMapper;
    private final GroupXmlMapper groupMapper;

    public GraphXmlMapper(BlockFactory blockFactory) throws JAXBException {

        this.xmlFactory = new ObjectFactory();
        this.blockMapper = new BlockXmlMapper(xmlFactory, blockFactory);
        this.connectionMapper = new ConnectionXmlMapper(xmlFactory);
        this.valueMapper = new ValueXmlMapper(xmlFactory);
        this.groupMapper = new GroupXmlMapper(xmlFactory);
    }

    public Graph toDomain(DocumentXml document, DocumentMetadata metadata) throws JAXBException {
        var blocks = blockMapper.toDomain(document.getBlocks().getBlock());
        var connections = connectionMapper.toDomain(document.getConnections().getConnection());
        var updatedBlocks = valueMapper.toDomain(blocks, document.getValues().getValue());
        var groups = groupMapper.toDomain(document.getGroups().getGroup());

        return new Graph(metadata, updatedBlocks, connections, groups);
    }

    public JAXBElement<DocumentXml> toXml(Graph graph) {

        var document = documentMetadataToXml(graph);
        var blocks = blockMapper.toXml(graph.blocks());
        var connections = connectionMapper.toXml(graph.connections());
        var values = valueMapper.toXml(graph.blocks());
        var groups = groupMapper.toXml(graph.groups());

        document.setBlocks(blocks);
        document.setConnections(connections);
        document.setValues(values);
        document.setGroups(groups);
        
        return xmlFactory.createDocument(document);

    }

    private DocumentXml documentMetadataToXml(Graph graph) {
    
        var document = xmlFactory.createDocumentXml();
        var metadata = graph.metadata();
        document.setZoomFactor(metadata.zoomFactor());
        document.setTranslateX(metadata.translateX());
        document.setTranslateY(metadata.translateY());
        return document;
    }

}

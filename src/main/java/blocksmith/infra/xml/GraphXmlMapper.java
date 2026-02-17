package blocksmith.infra.xml;

import blocksmith.app.GraphDocument;
import blocksmith.domain.block.BlockFactory;
import blocksmith.domain.graph.Graph;
import blocksmith.xml.v2.DocumentXml;
import blocksmith.xml.v2.ObjectFactory;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;

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

    public GraphDocument toDomain(DocumentXml document) throws JAXBException {
        var blocks = blockMapper.toDomain(document.getBlocks().getBlock());
        var connections = connectionMapper.toDomain(document.getConnections().getConnection());
        var updatedBlocks = valueMapper.toDomain(blocks, document.getValues().getValue());
        var groups = groupMapper.toDomain(document.getGroups().getGroup());
        var graph = Graph.withAll(updatedBlocks, connections, groups);

        return documentToDomain(document, graph);
    }

    private GraphDocument documentToDomain(DocumentXml document, Graph graph) {
        return new GraphDocument(
                graph,
                valueOrDefault(document.getZoomFactor(), GraphDocument.DEFAULT_ZOOM_FACTOR),
                valueOrDefault(document.getTranslateX(), GraphDocument.DEFAULT_TRANSLATE),
                valueOrDefault(document.getTranslateY(), GraphDocument.DEFAULT_TRANSLATE)
        );
    }

    private static double valueOrDefault(Double value, double fallback) {
        return value != null ? value : fallback;
    }

    public JAXBElement<DocumentXml> toXml(GraphDocument document) {

        var graph = document.graph();

        var documentXml = documentToXml(document);
        var blocksXml = blockMapper.toXml(graph.blocks());
        var connectionsXml = connectionMapper.toXml(graph.connections());
        var valuesXml = valueMapper.toXml(graph.blocks());
        var groupsXml = groupMapper.toXml(graph.groups());

        documentXml.setBlocks(blocksXml);
        documentXml.setConnections(connectionsXml);
        documentXml.setValues(valuesXml);
        documentXml.setGroups(groupsXml);

        return xmlFactory.createDocument(documentXml);

    }

    private DocumentXml documentToXml(GraphDocument document) {

        var documentXml = xmlFactory.createDocumentXml();
        documentXml.setZoomFactor(document.zoomFactor());
        documentXml.setTranslateX(document.translateX());
        documentXml.setTranslateY(document.translateY());
        return documentXml;
    }

}

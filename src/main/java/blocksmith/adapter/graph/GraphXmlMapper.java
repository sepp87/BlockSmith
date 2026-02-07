package blocksmith.adapter.graph;

import blocksmith.domain.block.Block;
import blocksmith.domain.block.EditorMetadata;
import blocksmith.domain.block.ValueInst;
import blocksmith.domain.connection.Connection;
import blocksmith.domain.graph.DocumentMetadata;
import blocksmith.domain.graph.Graph;
import blocksmith.domain.group.Group;
import blocksmith.xml.v2.BlockXml;
import blocksmith.xml.v2.ConnectionXml;
import blocksmith.xml.v2.DocumentXml;
import blocksmith.xml.v2.GroupXml;
import blocksmith.xml.v2.ObjectFactory;
import blocksmith.xml.v2.ValueXml;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author joost
 */
public class GraphXmlMapper {

    private final Unmarshaller unmarshaller;

    public GraphXmlMapper() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
        this.unmarshaller = context.createUnmarshaller();
    }

    public Graph toDomain(Path path, DocumentXml document) throws JAXBException {

        var metadata = documentMetadataToDomain(path, document);
        var blocks = blocksToDomain(document.getBlocks().getBlock());
        var connections = connectionsToDomain(document.getConnections().getConnection());
        var values = valuesToDomain(document.getValues().getValue());
        var groups = groupsToDomain(document.getGroups().getGroup());

        return null;
    }

    private DocumentMetadata documentMetadataToDomain(Path path, DocumentXml xmlDoc) {
        Double translateX = xmlDoc.getTranslateX();
        Double translateY = xmlDoc.getTranslateY();
        Double zoomFactor = xmlDoc.getZoomFactor();
        return DocumentMetadata.create(path, zoomFactor, translateX, translateY);
    }

    private List<Block> blocksToDomain(List<BlockXml> blocksXml) {
        var result = new ArrayList<Block>();

        for (var blockXml : blocksXml) {
            var id = blockXml.getId();
            var type = blockXml.getType();
            var label = blockXml.getLabel();
            var editorMetadata = editorMetadataToDomain(blockXml);
        }
        return result;
    }

    private EditorMetadata editorMetadataToDomain(BlockXml blockXml) {

        var x = blockXml.getX();
        var y = blockXml.getY();
        var width = blockXml.getWidth();
        var height = blockXml.getHeight();

        return new EditorMetadata(x, y, width, height);
    }

    private List<Connection> connectionsToDomain(List<ConnectionXml> connectionsXml) {
        var result = new ArrayList<Connection>();
        for (var connectionXml : connectionsXml) {

        }
        return result;
    }

    private List<ValueInst> valuesToDomain(List<ValueXml> valuesXml) {
        var result = new ArrayList<ValueInst>();
        for (var valueXml : valuesXml) {

        }
        return result;

    }

    private List<Group> groupsToDomain(List<GroupXml> groupsXml) {
        var result = new ArrayList<Group>();
        for (var groupXml : groupsXml) {

        }
        return result;

    }

}

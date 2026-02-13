package btscore.graph.io;

import blocksmith.domain.block.Block;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.Connection;
import blocksmith.domain.connection.PortRef;
import blocksmith.ui.MethodBlockNew;
import blocksmith.xml.v2.BlockRefXml;
import blocksmith.xml.v2.BlocksXml;
import blocksmith.xml.v2.ConnectionsXml;
import blocksmith.xml.v2.GroupsXml;
import blocksmith.xml.v2.ObjectFactory;
import blocksmith.xml.v2.ValueXml;
import blocksmith.xml.v2.ValuesXml;
import btscore.Config;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.File;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import btscore.workspace.WorkspaceModel;
import btscore.graph.group.BlockGroupModel;
import btscore.graph.block.BlockModel;
import btscore.graph.connection.ConnectionModel;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author joostmeulenkamp
 */
public class GraphSaverV2 {

    private static ObjectFactory objectFactory;

    public static ObjectFactory getObjectFactory() {
        if (objectFactory == null) {
            objectFactory = new ObjectFactory();
        }
        return objectFactory;
    }

    public static void serialize(File file, WorkspaceModel workspaceModel) {
        try {

            ObjectFactory factory = getObjectFactory();

            // serialize workspace and settings
            var documentTag = factory.createDocumentXml();
            documentTag.setZoomFactor(workspaceModel.zoomFactorProperty().get());
            documentTag.setTranslateX(workspaceModel.translateXProperty().get());
            documentTag.setTranslateY(workspaceModel.translateYProperty().get());

            // serialize blocks of graph
            Collection<BlockModel> blocks = workspaceModel.getBlockModels();
            var blocksTag = serializeBlockModels(blocks);
            documentTag.setBlocks(blocksTag);

            // serialize connections of graph
            Collection<ConnectionModel> connections = workspaceModel.getConnectionModels();
            var connectionsTag = serializeConnnectionModels(connections);
            documentTag.setConnections(connectionsTag);

            // serialize groups of graph
            Collection<BlockGroupModel> groups = workspaceModel.getBlockGroupModels();
            var groupsTag = serializeGroupModels(groups);
            documentTag.setGroups(groupsTag);

            var valuesTag = serializeValues(blocks);
            documentTag.setValues(valuesTag);

            // serialize the conplete document and save to file
            var document = factory.createDocument(documentTag);
            JAXBContext context = JAXBContext.newInstance("blocksmith.xml.v2");
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(document, file);

            // set file reference for quick save
            workspaceModel.fileProperty().set(file);

        } catch (JAXBException ex) {
            ex.printStackTrace();
            Logger.getLogger(GraphSaverV2.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private static BlocksXml serializeBlockModels(Collection<BlockModel> blocks) {
        ObjectFactory factory = getObjectFactory();
        var blocksTag = factory.createBlocksXml();
        for (var block : blocks) {
            var blockTag = factory.createBlockXml();
            blockTag.setId(block.getId());
            blockTag.setType(block.type());
            blockTag.setLabel(block.nameProperty().get());
            blockTag.setX(block.layoutXProperty().get());
            blockTag.setY(block.layoutYProperty().get());
            if (block.resizableProperty().get()) {
                blockTag.setWidth(block.widthProperty().get());
                blockTag.setHeight(block.heightProperty().get());
            }

            blocksTag.getBlock().add(blockTag);
        }
        return blocksTag;
    }

    private static ConnectionsXml serializeConnnectionModels(Collection<ConnectionModel> connections) {
        ObjectFactory factory = getObjectFactory();
        var connectionsTag = factory.createConnectionsXml();
        for (var connection : connections) {
            var connectionTag = factory.createConnectionXml();

            connectionTag.setFromBlock(connection.getStartPort().getBlock().getId());
            connectionTag.setFromPort(connection.getStartPort().nameProperty().get());
            connectionTag.setToBlock(connection.getEndPort().getBlock().getId());
            connectionTag.setToPort(connection.getEndPort().nameProperty().get());

            connectionsTag.getConnection().add(connectionTag);
        }
        return connectionsTag;
    }

    private static GroupsXml serializeGroupModels(Collection<BlockGroupModel> groups) {
        ObjectFactory factory = getObjectFactory();
        var groupsTag = factory.createGroupsXml();
        for (var group : groups) {
            var groupTag = factory.createGroupXml();
            groupTag.setLabel(group.nameProperty().get());
            var blockRefs = blockRefsToXml(group.getBlocks());
            groupTag.getBlock().addAll(blockRefs);
            groupsTag.getGroup().add(groupTag);
        }
        return groupsTag;
    }
    
    
    public static List<BlockRefXml> blockRefsToXml(Collection<BlockModel> blocks) {
        var result = new ArrayList<BlockRefXml>();
        for(var block : blocks) {
            var blockRefXml = getObjectFactory().createBlockRefXml();
            blockRefXml.setId(block.getId());
            result.add(blockRefXml);
        }
        return result;
    }

    public static ValuesXml serializeValues(Collection<BlockModel> blocks) {
        ObjectFactory factory = getObjectFactory();
        var valuesTag = factory.createValuesXml();

        for (var block : blocks) {
            if (block instanceof MethodBlockNew mb) {
                valuesTag.getValue().addAll(mb.serializeValues());
            }
        }

        return valuesTag;

    }

}

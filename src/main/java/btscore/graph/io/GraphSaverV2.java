package btscore.graph.io;

import blocksmith.xml.v2.Blocks;
import blocksmith.xml.v2.Connections;
import blocksmith.xml.v2.Groups;
import blocksmith.xml.v2.ObjectFactory;
import btscore.Config;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
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
            var documentTag = factory.createDocument();
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
            if (!groups.isEmpty()) {
                var groupsTag = serializeGroupModels(groups);
                documentTag.setGroups(groupsTag);
            }

            // serialize the conplete document and save to file
            var document = factory.createDocument(documentTag);
            JAXBContext context = JAXBContext.newInstance(Config.XML_NAMESPACE);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(document, file);

            // set file reference for quick save
            workspaceModel.fileProperty().set(file);

        } catch (JAXBException ex) {
            Logger.getLogger(GraphSaverV2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static Blocks serializeBlockModels(Collection<BlockModel> blocks) {
        ObjectFactory factory = getObjectFactory();
        var blocksTag = factory.createBlocks();
        for (var block : blocks) {
            var blockTag = factory.createBlock();
            blockTag.setId(block.getId());
            blockTag.setType(block.getId());
            blockTag.setLabel(block.getId());
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

    private static Connections serializeConnnectionModels(Collection<ConnectionModel> connections) {
        ObjectFactory factory = getObjectFactory();
        var connectionsTag = factory.createConnections();
        for (var connection : connections) {
            var connectionTag = factory.createConnection();
            connectionTag.setFromBlock(connection.getStartPort().getBlock().getId());
            connectionTag.setFromPort(connection.getStartPort().nameProperty().get());
            connectionTag.setToBlock(connection.getEndPort().getBlock().getId());
            connectionTag.setToPort(connection.getStartPort().nameProperty().get());
            
            connectionsTag.getConnection().add(connectionTag);
        }
        return connectionsTag;
    }

    private static Groups serializeGroupModels(Collection<BlockGroupModel> groups) {
        ObjectFactory factory = getObjectFactory();
        var groupsTag = factory.createGroups();
        for (var group : groups) {
            var groupTag = factory.createGroup();
            groupTag.setLabel(group.nameProperty().get());

            groupsTag.getGroup().add(groupTag);
        }
        return groupsTag;
    }

}

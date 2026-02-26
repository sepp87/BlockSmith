package btscore.graph.io;

import blocksmith.app.block.BlockDefLibrary;
import blocksmith.app.block.BlockFuncLibrary;
import blocksmith.infra.AppPaths;
import blocksmith.infra.blockloader.ClassIndex;
import blocksmith.infra.blockloader.CompositeBlockDefLoader;
import blocksmith.infra.blockloader.MethodBlockDefLoader;
import blocksmith.infra.blockloader.MethodBlockFuncLoader;
import blocksmith.infra.blockloader.MethodIndex;
import blocksmith.ui.BlockModelFactory;
import blocksmith.ui.control.MultilineTextInput;
import blocksmith.ui.control.NumberSliderInput;
import btscore.Launcher;
import btscore.UiApp;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import btsxml.*;
import btscore.workspace.WorkspaceModel;
import btscore.graph.block.BlockFactory;
import btscore.graph.group.BlockGroupModel;
import btscore.graph.block.BlockModel;
import btscore.graph.connection.ConnectionModel;
import btscore.graph.port.PortModel;
import java.util.Collection;
import java.util.UUID;
import javax.xml.namespace.QName;

/**
 * TODO DEPRECATE
 *
 * @author joostmeulenkamp
 */
public class GraphLoader {

    public static void deserialize(File file, WorkspaceModel workspaceModel) {
        try {
            JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            JAXBElement<?> document = (JAXBElement) unmarshaller.unmarshal(file);
            DocumentTag documentTag = (DocumentTag) document.getValue();

            // deserialize workspace and settings
            workspaceModel.setZoomFactor(documentTag.getScale());
            workspaceModel.translateXProperty().set(documentTag.getTranslateX());
            workspaceModel.translateYProperty().set(documentTag.getTranslateY());

            // deserialize blocks of graph
            BlocksTag blocksTag = documentTag.getBlocks();
            deserializeBlocks(blocksTag, workspaceModel);

            // deserialize connections of graph
            ConnectionsTag connectionsTag = documentTag.getConnections();
            deserializeConnections(connectionsTag, workspaceModel);

            // deserialize groups of graph
            GroupsTag groups = documentTag.getGroups();
            deserializeGroups(groups, workspaceModel);

            // register all transmitting ports, first after deserializing all blocks and connections
            Collection<BlockModel> blocks = workspaceModel.getBlockModels();
            Collection<ConnectionModel> autoConnections = workspaceModel.getAutoConnectIndex().registerAllTransmitters(blocks);
            if (!autoConnections.isEmpty() && UiApp.LOG_POTENTIAL_BUGS) {
                /**
                 * Edge case (which should not occur) - The end user opens and
                 * edits a .btsxml file in a text editor and removes
                 * auto-generated connections manually.
                 */
                System.out.println(autoConnections.size() + " AUTO CONNECTIONS GENERATED ON LOADING FROM FILE");
            }

            // set file reference for quick save
            workspaceModel.fileProperty().set(file);

        } catch (JAXBException | SecurityException | IllegalArgumentException ex) {
            Logger.getLogger(GraphLoader.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private static void deserializeBlocks(BlocksTag blocksTag, WorkspaceModel workspaceModel) {
        List<BlockTag> blockTagList = blocksTag.getBlock();
        if (blockTagList == null) {
            return;
        }

        for (BlockTag blockTag : blockTagList) {

            String blockIdentifier = blockTag.getType();
            if (Launcher.BLOCK_DEF_LOADER) {
                try {
                    var paths = new AppPaths();
                    var classIndex = new ClassIndex(paths);
                    var methodIndex = new MethodIndex(classIndex.classes());

                    var methodDefLoader = new MethodBlockDefLoader(methodIndex.methods());
                    var compositeDefLoader = new CompositeBlockDefLoader(List.of(methodDefLoader));
                    var defLibrary = new BlockDefLibrary(compositeDefLoader.load());

                    var methodFuncLoader = new MethodBlockFuncLoader(methodIndex.methods());
                    var funcLibrary = new BlockFuncLibrary(methodFuncLoader.load());

                    var factory = new BlockModelFactory(defLibrary, funcLibrary);
                    var block = factory.create(blockIdentifier, blockTag.getUUID());
                    block.layoutXProperty().set(blockTag.getX());
                    block.layoutYProperty().set(blockTag.getY());
                    if (block.resizableProperty().get()) {
                        Double width = blockTag.getWidth();
                        Double height = blockTag.getHeight();
                        if (width == null || height == null) {
                            return;
                        }
                        block.widthProperty().set(width);
                        block.heightProperty().set(height);
                    }
                    var controls = block.getInputControls();
                    for (var entry : controls.entrySet()) {
                        var valueId = entry.getKey();
                        var control = entry.getValue();

                        if (control.isEditable()) {
                            String value = blockTag.getOtherAttributes().get(QName.valueOf(valueId));
                            control.parseValue(value);
                            if (control instanceof NumberSliderInput slider) {
                                String min = blockTag.getOtherAttributes().get(QName.valueOf("min"));
                                String max = blockTag.getOtherAttributes().get(QName.valueOf("max"));
                                String step = blockTag.getOtherAttributes().get(QName.valueOf("step"));
                                slider.setMin(Double.parseDouble(min));
                                slider.setMax(Double.parseDouble(max));
                                slider.setStep(Double.parseDouble(step));
                            }
                        }
                    }
                    workspaceModel.addBlockModel(block);

                } catch (Exception e) {
                    Logger.getLogger(GraphLoader.class.getName()).log(Level.SEVERE, e.getMessage(), e);

                }

            } else {

                BlockModel blockModel = BlockFactory.createBlock(blockIdentifier);
                if (blockModel == null) {
                    System.out.println("WARNING: Could not instantiate block type " + blockIdentifier);
                    return;
                }
                blockModel.deserialize(blockTag);
                workspaceModel.addBlockModel(blockModel);
            }

        }
    }

    private static void deserializeConnections(ConnectionsTag connectionsTag, WorkspaceModel workspaceModel) {
        List<ConnectionTag> connectionTagList = connectionsTag.getConnection();
        if (connectionTagList == null) {
            return;
        }

        for (ConnectionTag connectionTag : connectionTagList) {

            String startBlockUuid = connectionTag.getStartBlock();
            int startPortIndex = connectionTag.getStartIndex();
            String endBlockUuid = connectionTag.getEndBlock();
            int endPortIndex = connectionTag.getEndIndex();

            BlockModel startBlock = null;
            BlockModel endBlock = null;
            for (BlockModel blockModel : workspaceModel.getBlockModels()) {
                if (blockModel.idProperty().get().compareTo(startBlockUuid) == 0) {
                    startBlock = blockModel;
                } else if (blockModel.idProperty().get().compareTo(endBlockUuid) == 0) {
                    endBlock = blockModel;
                }
            }

            if (startBlock != null && endBlock != null) {
                PortModel startPort = startBlock.getOutputPorts().get(startPortIndex);
                PortModel endPort = endBlock.getInputPorts().get(endPortIndex);
                workspaceModel.addConnectionModel(startPort, endPort);
            }

        }
    }

    private static void deserializeGroups(GroupsTag groupsTag, WorkspaceModel workspaceModel) {
        if (groupsTag == null) {
            return;
        }

        List<GroupTag> groupTagList = groupsTag.getGroup();
        if (groupTagList == null) {
            return;
        }

        for (GroupTag groupTag : groupTagList) {
//            BlockGroupModel group = new BlockGroupModel(workspaceController.getContextId(), workspaceController, workspaceModel);
//            BlockGroupModel group = new BlockGroupModel(workspaceModel);
//            group.deserialize(groupTag);
//            workspaceModel.addBlockGroupModel(group);

//            nameProperty().set(xmlTag.getName());
//            List<BlockReferenceTag> blockReferenceTagList = xmlTag.getBlockReference();
//            List<BlockModel> list = new ArrayList<>();
//            for (BlockReferenceTag blockReferenceTag : blockReferenceTagList) {
//                for (BlockModel block : workspaceModel.getBlockModels()) {
//                    if (block.idProperty().get().equals(blockReferenceTag.getUUID())) {
//                        list.add(block);
//                        break;
//                    }
//                }
//            }
//            setBlocks(list);
            BlockGroupModel group = new BlockGroupModel(UUID.randomUUID().toString(), workspaceModel.getBlockGroupIndex());
            group.nameProperty().set(groupTag.getName());
            List<BlockReferenceTag> blockReferenceTagList = groupTag.getBlockReference();
            List<BlockModel> list = new ArrayList<>();
            for (BlockReferenceTag blockReferenceTag : blockReferenceTagList) {
                for (BlockModel block : workspaceModel.getBlockModels()) {
                    if (block.idProperty().get().equals(blockReferenceTag.getUUID())) {
                        list.add(block);
                        break;
                    }
                }
            }
            group.setBlocks(list); // blocks should be set beforehand, because otherwise WorkspaceBlockGroupHelper.addBlockGroupModel() does NOT index grouped blocks
            workspaceModel.addBlockGroupModel(group);
        }
    }

}

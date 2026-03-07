package blocksmith.ui.graph.connection;

import blocksmith.ui.graph.port.PortModel;

/**
 *
 * @author joostmeulenkamp
 */
public class ConnectionFactory {

    public static ConnectionModel createConnection(PortModel startPort, PortModel endPort) {
        return new ConnectionModel(startPort, endPort);
    }
}

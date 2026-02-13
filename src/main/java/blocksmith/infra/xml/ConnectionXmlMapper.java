package blocksmith.infra.xml;

import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.Connection;
import blocksmith.domain.connection.PortRef;
import blocksmith.xml.v2.ConnectionXml;
import blocksmith.xml.v2.ConnectionsXml;
import blocksmith.xml.v2.ObjectFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author joost
 */
public class ConnectionXmlMapper {
    
    private final ObjectFactory xmlFactory;
    
    public ConnectionXmlMapper(ObjectFactory xmlFactory) {
        this.xmlFactory = xmlFactory;
    }
    
    public List<Connection> toDomain(List<ConnectionXml> connectionXmls) {
        var result = new ArrayList<Connection>();
        for (var connectionXml : connectionXmls) {
            
            var fromBlock = BlockId.from(connectionXml.getFromBlock());
            var fromPort = connectionXml.getFromPort();
            var toBlock = BlockId.from(connectionXml.getToBlock());
            var toPort = connectionXml.getToPort();
            
            var from = new PortRef(fromBlock, fromPort);
            var to = new PortRef(toBlock, toPort);
            
            var connection = new Connection(from, to);
            result.add(connection);
            
        }
        return result;
    }
    
    public ConnectionsXml toXml(Collection<Connection> connections) {
        var result = xmlFactory.createConnectionsXml();
        
        for (var connection : connections) {
            var connectionXml = xmlFactory.createConnectionXml();
            connectionXml.setFromBlock(connection.from().blockId().toString());
            connectionXml.setFromPort(connection.from().valueId());
            connectionXml.setToBlock(connection.to().blockId().toString());
            connectionXml.setToPort(connection.to().valueId());
            result.getConnection().add(connectionXml);
        }
        return result;
    }
}

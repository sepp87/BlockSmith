package blocksmith.domain.block;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author joostmeulenkamp
 */
public final class Block {

    private final UUID id;
    private final String type;
    private final List<Port> ports;

    public Block(UUID id, String type, List<Port> ports) {
        this.id = Objects.requireNonNull(id);
        this.type = Objects.requireNonNull(type);
        this.ports = List.copyOf(Objects.requireNonNull(ports));
    }

    public UUID id() {
        return id;
    }

    public String type() {
        return type;
    }

    public Collection<Port> ports() {
        return ports;
    }

    public Collection<Port> inputPorts() {
        return ports.stream().filter(port -> port.direction() == Port.Direction.INPUT).toList();
    }

    public Collection<Port> outputPorts() {
        return ports.stream().filter(port -> port.direction() == Port.Direction.OUTPUT).toList();
    }
}

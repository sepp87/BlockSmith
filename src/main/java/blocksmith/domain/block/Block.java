package blocksmith.domain.block;

import blocksmith.domain.value.Param;
import blocksmith.domain.value.Port;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author joostmeulenkamp
 */
public final class Block {

    private final BlockId id;
    private final String type;
    private final List<Port> ports;
    private final List<Param> params;
    private final EditorMetadata metadata;

    public Block(BlockId id, String type, Collection<Port> ports, Collection<Param> params, EditorMetadata metadata) {
        this.id = Objects.requireNonNull(id);
        this.type = Objects.requireNonNull(type);
        this.ports = List.copyOf(Objects.requireNonNull(ports));
        this.params = List.copyOf(Objects.requireNonNull(params));
        this.metadata = metadata;
    }

    public BlockId id() {
        return id;
    }

    public String type() {
        return type;
    }

    public Collection<Port> ports() {
        return ports;
    }
    
    public Collection<Param> params() {
        return params;
    }

    public Optional<EditorMetadata> editorMetadata() {
        return Optional.ofNullable(metadata);
    }

    public Collection<Port> inputPorts() {
        return ports.stream().filter(port -> port.direction() == Port.Direction.INPUT).toList();
    }

    public Collection<Port> outputPorts() {
        return ports.stream().filter(port -> port.direction() == Port.Direction.OUTPUT).toList();
    }

    public Block withParamValue(String valueId, String value) {
        var updated = new ArrayList<>();
        for(var p : params) {
            if(p.valueId().equals(valueId)) {
                updated.add(p.withValue(value));
            } else {
                updated.add(p);
            }
        }
        
        var index = new HashMap<String, Param>();
        params.forEach(p -> index.put(p.valueId(), p));
        index.put(param.valueId(), param);

        return new Block(
                id,
                type,
                ports,
                index.values(),
                metadata
        );
    }

    public Block withEditorMetadata(EditorMetadata metadata) {
        return new Block(
                id,
                type,
                ports,
                params,
                metadata
        );
    }

}

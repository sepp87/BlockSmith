package blocksmith.domain.block;

import blocksmith.domain.value.Param;
import blocksmith.domain.value.Port;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 *
 * @author joostmeulenkamp
 */
public final class Block {

    private final BlockId id;
    private final String type;
    private final List<Param> params;
    private final List<Port> ports;
    private final BlockLayout layout;

    public Block(BlockId id, String type, Collection<Param> params, Collection<Port> ports, BlockLayout layout) {
        this.id = Objects.requireNonNull(id);
        this.type = Objects.requireNonNull(type);
        this.params = List.copyOf(Objects.requireNonNull(params));
        this.ports = List.copyOf(Objects.requireNonNull(ports));
        this.layout = layout == null ? BlockLayout.createEmpty() : layout;
    }

    public BlockId id() {
        return id;
    }

    public String type() {
        return type;
    }

    public Collection<Param> params() {
        return params;
    }

    public Collection<Port> ports() {
        return ports;
    }

    public BlockLayout layout() {
        return layout;
    }

    public Optional<Param> param(String valueId) {
        return params.stream().filter(param -> param.valueId().equals(valueId)).findFirst();
    }

    public Optional<Port> port(String valueId) {
        return ports.stream().filter(port -> port.valueId().equals(valueId)).findFirst();
    }

    public Collection<Port> inputPorts() {
        return ports.stream().filter(port -> port.direction() == Port.Direction.INPUT).toList();
    }

    public Collection<Port> outputPorts() {
        return ports.stream().filter(port -> port.direction() == Port.Direction.OUTPUT).toList();
    }

    public Block withParamValue(String valueId, String value) {
        return withParamUpdated(valueId, (param) -> param.withValue(value));
    }

    public Block withParamActivated(String valueId) {
        return withParamUpdated(valueId, Param::activate);
    }

    public Block withParamDeactivated(String valueId) {
        return withParamUpdated(valueId, Param::deactivate);
    }

    private Block withParamUpdated(String valueId, Function<Param, Param> func) {

        var updated = new ArrayList<Param>();
        for (var p : params) {
            if (p.valueId().equals(valueId)) {
                updated.add(func.apply(p));
            } else {
                updated.add(p);
            }
        }

        return new Block(
                id,
                type,
                updated,
                ports,
                layout
        );
    }

    public Block withLabel(String label) {
        return new Block(
                id,
                type,
                params,
                ports,
                layout.withLabel(label)
        );
    }

    public Block withPosition(double x, double y) {
        return new Block(
                id,
                type,
                params,
                ports,
                layout.withPosition(x, y)
        );
    }

    public Block withSize(double width, double height) {
        return new Block(
                id,
                type,
                params,
                ports,
                layout.withSize(width, height)
        );
    }

    public Block withLayout(BlockLayout layout) {
        return new Block(
                id,
                type,
                params,
                ports,
                layout
        );
    }

    public Block duplicate(BlockId id) {
        return new Block(
                id,
                type,
                params,
                ports,
                layout
        );
    }

}

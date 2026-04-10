package blocksmith.infra.blockloader;

import blocksmith.domain.block.BlockDef;
import blocksmith.domain.block.OutputExtractor;
import blocksmith.domain.value.ParamDef;
import blocksmith.domain.value.ParamInput;
import blocksmith.domain.value.Port;
import blocksmith.domain.value.PortDef;
import blocksmith.domain.value.ValueType;
import blocksmith.utils.icons.FontAwesomeIcon;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author joost
 */
public class BlockDefBuilder {

    private int incoming = 0;
    private int outgoing = 0;

    private String type;
    private String name;
    private String description;
    private String category;
    private FontAwesomeIcon icon;
    private List<String> tags = new ArrayList<>();
    private List<String> aliases = new ArrayList<>();
    private List<ParamDef> params = new ArrayList<>();
    private List<PortDef> inputs = new ArrayList<>();
    private List<PortDef> outputs = new ArrayList<>();
    private OutputExtractor outputExtractor;
    private boolean hasAggregatedInput;

    public BlockDefBuilder type(String value) {
        this.type = value;
        return this;
    }

    public BlockDefBuilder name(String value) {
        this.name = value;
        return this;
    }

    public BlockDefBuilder description(String value) {
        this.description = value;
        return this;
    }

    public BlockDefBuilder category(String value) {
        this.category = value;
        return this;
    }

    public BlockDefBuilder icon(FontAwesomeIcon value) {
        this.icon = value;
        return this;
    }

    public BlockDefBuilder tags(String... values) {
        tags.addAll(List.of(values));
        return this;
    }

    public BlockDefBuilder aliases(String... values) {
        aliases.addAll(List.of(values));
        return this;
    }

//    public BlockDefBuilder param (String id, ParamInput input) {
//        ParamInput.
//        var argIndex = incoming;
//        var param = new ParamDef(name, argIndex, name, ValueType.of(String.class), Port.Direction.INPUT);
//        incoming++;
//    }
    public BlockDefBuilder input(String valueId, Class<?> rawType) {
        var argIndex = incoming;
        var isAggregateValue = false;
        
        var input = new PortDef(
                valueId,
                argIndex,
                valueId,
                Port.Direction.INPUT,
                ValueType.of(rawType),
                hasAggregatedInput,
                hasAggregatedInput,
                hasAggregatedInput
        );
        inputs.add(input);
        incoming++;
        return this;
    }

    public BlockDefBuilder params(List<String> values) {
        this.tags = values;
        return this;
    }

    public BlockDefBuilder inputs(List<String> values) {
        this.aliases = values;
        return this;
    }

    public BlockDefBuilder outputs(List<String> values) {
        this.tags = values;
        return this;
    }

    public BlockDef build() {

        var hasAggregatedInput = false;
        return new BlockDef(
                type,
                name,
                description,
                category,
                tags,
                aliases,
                icon,
                params,
                inputs,
                outputs,
                outputExtractor,
                hasAggregatedInput
        );
    }

}

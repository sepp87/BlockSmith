package blocksmith.ui.graph.block;

import blocksmith.ui.graph.block.MethodBlockNew;
import blocksmith.ui.control.TextInput;
import blocksmith.ui.control.InputControl;
import blocksmith.app.block.BlockDefLibrary;
import blocksmith.app.block.BlockFuncLibrary;
import blocksmith.infra.blockloader.ScannedBlockLibrary;
import blocksmith.domain.block.ArrayBlock;
import blocksmith.domain.block.Block;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.PortRef;
import blocksmith.domain.value.ParamDef;
import blocksmith.domain.value.ParamInput;
import blocksmith.domain.value.ParamInput.NumericType;
import blocksmith.domain.value.PortDef;
import blocksmith.domain.value.ValueType.SimpleType;
import blocksmith.domain.value.types.DataSheet;
import blocksmith.ui.control.BooleanInput;
import blocksmith.ui.control.ChoiceInput;
import blocksmith.ui.control.ColorInput;
import blocksmith.ui.control.DateInput;
import blocksmith.ui.control.DirectoryInput;
import blocksmith.ui.control.DoubleSliderInput;
import blocksmith.ui.control.FilePathInput;
import blocksmith.ui.control.FileTargetInput;
import blocksmith.ui.control.IntegerSliderInput;
import blocksmith.ui.control.MultilineTextInput;
import blocksmith.ui.control.PasswordInput;
import blocksmith.ui.display.DataSheetDisplay;
import blocksmith.ui.display.GenericDisplay;
import blocksmith.ui.display.ValueDisplay;
import java.util.Optional;
import java.util.logging.Logger;

/**
 *
 * @author joostmeulenkamp
 */
public class BlockModelFactory {

    private static final Logger LOGGER = Logger.getLogger(BlockModelFactory.class.getName());

    private final ScannedBlockLibrary blockLibrary;

    public BlockModelFactory(ScannedBlockLibrary blockLibrary) {
        this.blockLibrary = blockLibrary;
    }

    public MethodBlockNew create(Block domain) {
        var def = blockLibrary.defs().resolve(domain.type())
                .orElseThrow(() -> new IllegalArgumentException("Type does not exist: " + domain.type()));

        var type = def.type();
        var func = blockLibrary.funcs().resolve(type).get();

        var id = domain.id().toString();
        var block = new MethodBlockNew(def, id);

        for (var port : def.inputs()) {
            if (port.isAggregatedValue()) {
                continue;
            }
            block.addInputPort(port.valueId(), port.valueId(), port.valueType());
            var ref = PortRef.of(BlockId.from(id), port.direction(), port.valueId());
            valueDisplayFrom(port).ifPresent(display -> block.addValueDisplay(ref, display));
        }

        for (var port : domain.inputPorts()) {
            if (port.isElement()) {
                block.addInputPort(port.valueId(), port.valueId(), port.valueType());
            }
        }

        for (var port : def.outputs()) {
            block.addOutputPort(port.valueId(), port.valueName(), port.valueType());
            var ref = PortRef.of(BlockId.from(id), port.direction(), port.valueId());
            valueDisplayFrom(port).ifPresent(display -> block.addValueDisplay(ref, display));
        }

        for (var param : def.params()) {
            var control = inputControlFrom(param);
            block.addInputControl(param.valueId(), control);
        }

        return block;
    }
    private static Optional<ValueDisplay> valueDisplayFrom(PortDef port) {

        if (!port.display()) {
            return Optional.empty();
        }

        if (port.valueType() instanceof SimpleType type && type.raw() == DataSheet.class) {
            return Optional.of(new DataSheetDisplay());
        }

        var display = new GenericDisplay();
        return Optional.of(display);

    }

    private static InputControl<?> inputControlFrom(ParamDef param) {

        var valueId = param.valueId();
        var spec = param.input();

        return switch (spec) {
            case ParamInput.Unspecified na -> {
                BlockModelFactory.LOGGER.warning("Param has NO input spec. Using plain text input as fallback.");
                yield new TextInput(valueId);
            }

            case ParamInput.Boolean bool ->
                new BooleanInput(valueId);

            case ParamInput.Choice choice ->
                new ChoiceInput(valueId, choice.options().getFirst(), choice.options());

            case ParamInput.Color color ->
                new ColorInput(valueId);

            case ParamInput.Date date ->
                new DateInput(valueId);

            case ParamInput.Directory directory ->
                new DirectoryInput(valueId);

            case ParamInput.FilePath filePath ->
                new FilePathInput(valueId);

            case ParamInput.FileTarget fileTarger ->
                new FileTargetInput(valueId);

            case ParamInput.MultilineText multilineText ->
                new MultilineTextInput(valueId);

            case ParamInput.Password password ->
                new PasswordInput(valueId);

            case ParamInput.Range range ->
                inferNumberSliderFrom(valueId, range.type());

            case ParamInput.Text text ->
                new TextInput(valueId);
        };
    }

    private static InputControl<?> inferNumberSliderFrom(String valueId, NumericType type) {
        return switch (type) {
            case INTEGER ->
                new IntegerSliderInput(valueId);
            case DECIMAL ->
                new DoubleSliderInput(valueId);
        };
    }

}

package blocksmith.ui.graph.block;

import blocksmith.ui.graph.block.MethodBlockNew;
import blocksmith.ui.control.TextInput;
import blocksmith.ui.control.InputControl;
import blocksmith.app.block.BlockDefLibrary;
import blocksmith.app.block.BlockFuncLibrary;
import blocksmith.domain.value.ParamDef;
import blocksmith.domain.value.ParamInput;
import blocksmith.domain.value.ParamInput.NumericType;
import blocksmith.domain.value.PortDef;
import blocksmith.domain.value.ValueType;
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
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joostmeulenkamp
 */
public class BlockModelFactory {

    private static final Logger LOGGER = Logger.getLogger(BlockModelFactory.class.getName());

    private final BlockDefLibrary defLibrary;
    private final BlockFuncLibrary funcLibrary;

    public BlockModelFactory(BlockDefLibrary defLibrary, BlockFuncLibrary funcLibrary) {
        this.defLibrary = defLibrary;
        this.funcLibrary = funcLibrary;
    }

    public MethodBlockNew create(String type) {
        var id = UUID.randomUUID().toString();
        return create(type, id);
    }

    public MethodBlockNew create(String type, String id) {
        var oDef = defLibrary.resolve(type);

        if (oDef.isEmpty()) {
            throw new IllegalArgumentException("Type does not exist: " + type);
        }

        var def = oDef.get();
        type = def.type();
        var func = funcLibrary.findByType(type).get();

        var block = new MethodBlockNew(def, func, id);

        for (var input : def.inputs()) {
            block.addInputPort(input.valueId(), input.valueName(), input.valueType(), ValueType.toDataType(input.valueType()));
            valueDisplayFrom(input).ifPresent(display -> block.addValueDisplay(input.direction(), input.valueId(), display));
        }

        for (var output : def.outputs()) {
            block.addOutputPort(output.valueId(), output.valueName(), output.valueType(), ValueType.toDataType(output.valueType()));
            valueDisplayFrom(output).ifPresent(display -> block.addValueDisplay(output.direction(), output.valueId(), display));
        }

        for (var param : def.params()) {
            var control = inputControlFrom(param);
            block.addInputControl(param.valueId(), control);
        }

        block.isListOperator = def.isListOperator();
        block.isListWithUnknownReturnType = def.outputs().getFirst().valueType() instanceof ValueType.ListType listType && listType.elementType() instanceof ValueType.VarType;

        return block;
    }

    private static Optional<ValueDisplay> valueDisplayFrom(PortDef port) {

        if (!port.display()) {
            return Optional.empty();
        }
        
        if(port.valueType() instanceof SimpleType type && type.raw() == DataSheet.class) {
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

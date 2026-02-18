package blocksmith.ui;

import blocksmith.ui.control.TextInput;
import blocksmith.ui.control.InputControl;
import blocksmith.app.block.BlockDefLibrary;
import blocksmith.app.block.BlockFuncLibrary;
import blocksmith.app.logging.IdFormatter;
import blocksmith.domain.block.Block;
import blocksmith.domain.value.ParamDef;
import blocksmith.domain.value.ParamInput;
import blocksmith.domain.value.ParamInput.NumericType;
import blocksmith.domain.value.ValueType;
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
import java.util.UUID;

/**
 *
 * @author joostmeulenkamp
 */
public class BlockModelFactory {

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
            System.out.println("INPUT " + IdFormatter.shortId(UUID.fromString(id)) + "." + input.valueId());
            block.addInputPort(input.valueId(), ValueType.toDataType(input.valueType()));
        }

        for (var output : def.outputs()) {
            System.out.println("OUTPUT " + IdFormatter.shortId(UUID.fromString(id)) + "." + output.valueId());
            block.addOutputPort(output.valueId(), ValueType.toDataType(output.valueType()));
        }

        for (var param : def.params()) {
            var control = inputControlFrom(param);
            block.addInputControl(param.valueId(), control);
        }

        block.isListOperator = def.isListOperator();
        block.isListWithUnknownReturnType = def.outputs().getFirst().valueType() instanceof ValueType.ListType listType && listType.elementType() instanceof ValueType.VarType;

        return block;
    }

    private static InputControl<?> inputControlFrom(ParamDef param) {

        var spec = param.input();

        return switch (spec) {
            case ParamInput.NoInputSpec na ->
                inferInputControlFrom(param.valueType());

            case ParamInput.Boolean bool ->
                new BooleanInput();

            case ParamInput.Choice choice ->
                new ChoiceInput(choice.options().getFirst(), choice.options());

            case ParamInput.Color color ->
                new ColorInput();

            case ParamInput.Date date ->
                new DateInput();

            case ParamInput.Directory directory ->
                new DirectoryInput();

            case ParamInput.FilePath filePath ->
                new FilePathInput();

            case ParamInput.FileTarget fileTarger ->
                new FileTargetInput();

            case ParamInput.MultilineText multilineText ->
                new MultilineTextInput();

            case ParamInput.Password password ->
                new PasswordInput();

            case ParamInput.Range range ->
                inferNumberSliderFrom(range.type());

            case ParamInput.Text text ->
                new TextInput();
        };
    }

    private static InputControl<?> inferNumberSliderFrom(NumericType type) {
        return switch (type) {
            case INT ->
                new IntegerSliderInput();
            case DOUBLE ->
                new DoubleSliderInput();
        };
    }

    private static InputControl<?> inferInputControlFrom(ValueType valueType) {

        Class<?> rawType = null;

        if (valueType instanceof ValueType.ListType) {
            throw new IllegalArgumentException("Unsupported param type: " + valueType);

        } else if (valueType instanceof ValueType.SimpleType simple) {
            rawType = simple.raw();

        } else if (valueType instanceof ValueType.VarType var) {
            rawType = Object.class;
        }

        if (rawType == String.class) {
            return new TextInput();

        }
        if (rawType == Boolean.class || rawType == boolean.class) {
            return new BooleanInput();

        }
        if (rawType == Object.class) {
            return new MultilineTextInput();

        }
//        else if (dataType == Integer.class || dataType == Double.class) {
//            return new ParamInput.Range();
//
//        } else if (dataType == Path.class) {
//            return new ParamInput.FilePath();
//
//        } else if (dataType == Date.class) {
//            return new ParamInput.Date();
//
//        }

        throw new IllegalArgumentException("Unsupported param type: " + rawType.getSimpleName());
    }

}

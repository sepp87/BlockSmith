package blocksmith.ui;

import blocksmith.ui.control.TextInput;
import blocksmith.ui.control.InputControl;
import blocksmith.app.BlockDefLibrary;
import blocksmith.app.BlockFuncLibrary;
import blocksmith.domain.block.ParamDef;
import blocksmith.domain.block.ParamInput;
import blocksmith.domain.block.ParamInput.NumericType;
import blocksmith.domain.block.ValueType;
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
import btscore.graph.block.BlockModel;

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

    public BlockModel create(String type) {
        var def = defLibrary.findByType(type).get();
        var func = funcLibrary.findByType(type).get();

        var block = new MethodBlockNew(def, func);

        for (var input : def.inputs()) {
            block.addInputPort(input.name(), ValueType.toDataType(input.valueType()));
        }

        for (var output : def.outputs()) {
            block.addOutputPort(output.name(), ValueType.toDataType(output.valueType()));
        }

        for (var param : def.params()) {
            var control = inputControlFrom(param);
            block.addInputControl(param.name(), control);
        }

        block.isListOperator = def.isListOperator();
        block.isListWithUnknownReturnType = def.outputs().getFirst().valueType() instanceof ValueType.ListType listType && listType.elementType() instanceof ValueType.VarType;

        return block;
    }

    private static InputControl<?> inputControlFrom(ParamDef param) {

        var spec = param.input();

        return switch (spec) {
            case ParamInput.NoInputSpec na ->
                inferInputControlFrom(param.dataType());

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

    private static InputControl<?> inferInputControlFrom(Class<?> dataType) {
        if (dataType == String.class) {
            return new TextInput();

        }
        if (dataType == Boolean.class || dataType == boolean.class) {
            return new BooleanInput();

        }
        if (dataType == Object.class) {
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

        throw new IllegalArgumentException("Unsupported param type: " + dataType.getSimpleName());
    }

}

package blocksmith.ui;

import blocksmith.ui.control.TextInput;
import blocksmith.ui.control.InputControl;
import blocksmith.app.BlockDefLibrary;
import blocksmith.app.BlockFuncLibrary;
import blocksmith.domain.block.ParamDef;
import blocksmith.domain.block.ParamInput;
import blocksmith.ui.control.BooleanInput;
import blocksmith.ui.control.DirectoryInput;
import blocksmith.ui.control.FilePathInput;
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
            block.addInputPort(input.name(), input.dataType());
        }

        for (var output : def.outputs()) {
            block.addOutputPort(output.name(), output.dataType());
        }

        for (var param : def.params()) {
            var control = inputControlFrom(param);
            block.addInputControll(param.name(), control);
        }

        block.isListOperator = def.isListOperator();
        block.isListWithUnknownReturnType = def.outputs().getFirst().dataTypeIsGeneric();

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
                throw new UnsupportedOperationException("Unsupported param input: " + spec.getClass().getSimpleName());

            case ParamInput.Color color ->
                throw new UnsupportedOperationException("Unsupported param input: " + spec.getClass().getSimpleName());

            case ParamInput.Date date ->
                throw new UnsupportedOperationException("Unsupported param input: " + spec.getClass().getSimpleName());

            case ParamInput.Directory directory ->
                new DirectoryInput();

            case ParamInput.FilePath filePath ->
                new FilePathInput();

            case ParamInput.Password password ->
                new PasswordInput();

            case ParamInput.Range range ->
                throw new UnsupportedOperationException("Unsupported param input: " + spec.getClass().getSimpleName());

            case ParamInput.Text text ->
                new TextInput();
        };
    }

    private static InputControl<?> inferInputControlFrom(Class<?> dataType) {
        if (dataType == String.class) {
            return new TextInput();

        } else if (dataType == Boolean.class || dataType == boolean.class) {
            return new BooleanInput();
            
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

package blocksmith.ui.control;

import java.io.File;
import javafx.stage.FileChooser;

/**
 *
 * @author joost
 */
public class FileTargetInput extends AbstractPathInput {

    public FileTargetInput() {
        textField.setPromptText("Save to file...");
        textField.setEditable(false);
    }

    @Override
    protected File choosePath() {
        var picker = new FileChooser();
        picker.setTitle("Save as...");
        var window = button.getScene().getWindow();
        var file = picker.showSaveDialog(window);
        return file;
    }

    @Override
    public void setEditable(boolean isEditable) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @Override
    public InputControl<String> copy() {
        var control = new FileTargetInput();
        if (isEditable()) {
            control.setValue(this.getValue());
        }
        return control;
    }

}

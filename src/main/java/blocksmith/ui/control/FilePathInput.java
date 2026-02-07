package blocksmith.ui.control;

import java.io.File;
import javafx.stage.FileChooser;

/**
 *
 * @author joost
 */
public class FilePathInput extends AbstractPathInput {

    public FilePathInput() {
        textField.setPromptText("Open a file...");

    }

    @Override
    protected File choosePath() {
        var picker = new FileChooser();
        picker.setTitle("Choose a file...");
        var window = button.getScene().getWindow();
        var file = picker.showOpenDialog(window);
        return file;
    }

    @Override
    protected void onEditableChanged(boolean isEditable) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public InputControl<String> copy() {
        var control = new FilePathInput();
        if(isEditable()) {
            control.setValue(this.getValue());
        }
        return control;
    }

}

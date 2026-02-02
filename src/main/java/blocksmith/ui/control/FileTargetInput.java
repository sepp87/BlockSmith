package blocksmith.ui.control;

import java.io.File;
import javafx.stage.FileChooser;

/**
 *
 * @author joost
 */
public class FileTargetInput extends FilePathInput {

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

}

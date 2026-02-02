package blocksmith.ui.control;

import java.io.File;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

/**
 *
 * @author joost
 */
public class DirectoryInput extends AbstractPathInput {

    public DirectoryInput() {
        textField.setPromptText("Open a directory...");

    }

    @Override
    protected File choosePath() {
        var picker = new DirectoryChooser();
        picker.setTitle("Choose a directory...");
        var window = button.getScene().getWindow();
        var file = picker.showDialog(window);
        return file;
    }

}

package btscore.editor.tab;

import java.util.UUID;
import javafx.scene.Node;

/**
 *
 * @author joost
 */
public record TabContent(
        String id,
        String label,
        Node view) {

}

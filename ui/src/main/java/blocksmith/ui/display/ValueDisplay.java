package blocksmith.ui.display;

import java.util.List;
import javafx.scene.Node;

/**
 *
 * @author joost
 */
public sealed interface ValueDisplay {

    Node node();

    void dispose();

    public non-sealed interface SingleValue extends ValueDisplay {

        void render(Object value);
    }

    public non-sealed interface MultiValue extends ValueDisplay {

        void render(List<?> values);
    }

}

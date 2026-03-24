package blocksmith.exec;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author joost
 */
public class IntermediateResult {

    private final List<BlockException> exceptions = new ArrayList<>();

    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public List<BlockException> exceptions() {
        return exceptions;
    }

}

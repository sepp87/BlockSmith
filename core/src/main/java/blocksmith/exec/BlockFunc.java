package blocksmith.exec;

import java.util.List;

/**
 *
 * @author joost
 */
@FunctionalInterface
public interface BlockFunc extends BlockExecutable {

    Object apply(List<Object> inputs) throws Exception;
}

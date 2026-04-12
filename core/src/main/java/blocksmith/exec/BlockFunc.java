package blocksmith.exec;

import java.util.List;

/**
 *
 * @author joost
 */
@FunctionalInterface
public non-sealed interface BlockFunc extends BlockExec {

    Object apply(List<Object> inputs) throws Exception;
}

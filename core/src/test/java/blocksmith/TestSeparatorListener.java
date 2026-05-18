package blocksmith;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;

/**
 *
 * @author joost
 */
public class TestSeparatorListener implements TestExecutionListener {

    @Override
    public void executionStarted(TestIdentifier id) {
        if (id.isTest()) {
            return;           // class/container only
        }
        System.out.println(); // blank line after "Running ..."
    }

    @Override
    public void executionFinished(TestIdentifier id, TestExecutionResult result) {
        if (id.isTest()) {
            return;
        }
        System.out.println(); // blank line before "Tests run: ..."
    }
}

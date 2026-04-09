package blocksmith;

import blocksmith.infra.AppPaths;
import blocksmith.utils.OperatingSystem;
import blocksmith.utils.SystemUtils;
import java.io.IOException;

/**
 *
 * @author joost
 */
public record Environment(Profile profile, AppPaths paths) {

    public enum Profile {
        DEV, PROD, TEST
    }

    public boolean isDev() {
        return profile == Profile.DEV;
    }

    public boolean isProd() {
        return profile == Profile.PROD;
    }

    public boolean isTest() {
        return profile == Profile.TEST;
    }

    public static Environment dev() throws IOException {
        return create(Profile.DEV);
    }

    public static Environment prod() throws IOException {
        return create(Profile.PROD);
    }

    public static Environment test() throws IOException {
        return create(Profile.TEST);
    }

    private static Environment create(Profile profile) throws IOException {
        var paths = AppPaths.create();
        return new Environment(
                profile,
                paths
        );
    }
}

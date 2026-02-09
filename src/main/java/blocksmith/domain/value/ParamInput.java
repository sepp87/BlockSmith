package blocksmith.domain.value;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author joostmeulenkamp
 */
public sealed interface ParamInput {

    public enum NumericType {
        INT,
        DOUBLE
    }

    record Boolean() implements ParamInput {
    }

    record Text() implements ParamInput {
    }

    record MultilineText() implements ParamInput {
    }

    record Password() implements ParamInput {
    }

    record Range(NumericType type) implements ParamInput {

        public Range {
            Objects.requireNonNull(type);
        }
    }

    record Choice(List<String> options) implements ParamInput {

    }

    record FilePath() implements ParamInput {
    }

    record FileTarget() implements ParamInput {
    }

    record Directory() implements ParamInput {
    }

    record Date() implements ParamInput {
    }

    record Color() implements ParamInput {
    }

    record NoInputSpec() implements ParamInput {
    }

}

/**
 * date > date picker path > file picker color > color picker
 */
//sealed interface UserInputSpec {
//
//    record Slider(
//        int min,
//        int max,
//        int step
//    ) implements UserInputSpec {}
//
//    record Password() implements UserInputSpec {}
//
//    record Multiline(
//        int rows
//    ) implements UserInputSpec {}
//
//    record FilePath() implements UserInputSpec {}
//}

package blocksmith.domain.value;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author joostmeulenkamp
 */
public sealed interface ParamInput {

    public enum NumericType {
        INTEGER,
        DECIMAL
    }

    record Boolean() implements ParamInput {
    }

    record Text() implements ParamInput {
    }

    record MultilineText() implements ParamInput {
    }

    record Password() implements ParamInput {
    }

    record Range(NumericType type, double min, double max, double step) implements ParamInput {

        public Range    {
            Objects.requireNonNull(type);
        }

        public static Range ofInteger() {
            return new Range(NumericType.INTEGER, 0, 10, 1);
        }

        public static Range ofDecimal() {
            return new Range(NumericType.DECIMAL, 0, 10, 0.1);
        }
        
        public Range withBounds (double min, double max, double step) {
            return new Range(type, min, max, step);
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

    record Unspecified() implements ParamInput {
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

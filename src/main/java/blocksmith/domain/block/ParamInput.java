package blocksmith.domain.block;

/**
 *
 * @author joostmeulenkamp
 */
public sealed interface ParamInput {
    
    record Boolean() implements ParamInput {
        
    }

    record Text() implements ParamInput {
    }

    record Password() implements ParamInput {
    }

    record Range() implements ParamInput {
    }

    record Choice() implements ParamInput {
    }

    record FilePath() implements ParamInput {
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
 * date > date picker
 * path > file picker
 * color > color picker
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

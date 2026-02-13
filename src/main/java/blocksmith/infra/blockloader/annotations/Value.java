package blocksmith.infra.blockloader.annotations;

import blocksmith.domain.value.ParamInput;
import blocksmith.domain.value.ParamInput.NoInputSpec;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * an Annotation class for method params e.g.
 *
 *
 * // @BlockMetadata ...
 *
 * // public static String stringInput(@UserInput Int value ) { ... }
 *
 * then the user/factory can map that parameter to decide if he or she wants a
 * TextField or a NumberSlider
 *
 * @author joost
 */
@Target({ElementType.PARAMETER}) // On method parameter
@Retention(RetentionPolicy.RUNTIME)
public @interface Value {

    String id() default "";
    Class<? extends ParamInput> input() default NoInputSpec.class;
    Source source() default Source.PARAM;
           
    public enum Source {
        PARAM,
        PORT,
        PARAM_OR_PORT
    }
            
}

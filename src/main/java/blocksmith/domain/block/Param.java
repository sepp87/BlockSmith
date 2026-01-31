package blocksmith.domain.block;

import blocksmith.domain.block.ParamInput.NoInputSpec;
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
@Target({ElementType.PARAMETER}) //On class and method level
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {

    Class<? extends ParamInput> input() default NoInputSpec.class;
}

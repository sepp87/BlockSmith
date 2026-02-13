package blocksmith.infra.blockloader.annotations;

import btscore.icons.FontAwesomeSolid;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author joost
 */
@Target({ElementType.TYPE, ElementType.METHOD}) //On class and method level
@Retention(RetentionPolicy.RUNTIME)
public @interface Block {

    String type();

    String name() default "";

    String description() default "";

    String category();

    String[] tags() default {};

    String[] aliases() default {};

    FontAwesomeSolid icon() default FontAwesomeSolid.NULL;

//    Method declarations must not have any parameters or a throws clause. 
//    Return types are restricted to primitives, String, Class, enums, 
//    annotations, and arrays of the preceding types.
}

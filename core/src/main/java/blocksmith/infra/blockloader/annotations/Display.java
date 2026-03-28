

package blocksmith.infra.blockloader.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author joost
 */
@Target({ElementType.PARAMETER, ElementType.TYPE_USE}) // On method parameter and return type
@Retention(RetentionPolicy.RUNTIME)
public @interface Display {

}

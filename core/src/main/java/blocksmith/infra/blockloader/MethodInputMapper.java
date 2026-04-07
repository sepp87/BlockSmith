package blocksmith.infra.blockloader;

import blocksmith.domain.value.PortDef;
import static blocksmith.domain.value.Port.Direction.INPUT;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import blocksmith.infra.blockloader.annotations.Value;
import static blocksmith.infra.blockloader.annotations.Value.Source.PARAM;

/**
 *
 * @author joost
 */
public class MethodInputMapper {
    
    public static List<PortDef> map(Method method) {
        var result = new ArrayList<PortDef>();

        int argIndex = 0;
        for (Parameter parameter : method.getParameters()) {
            Value value = parameter.getAnnotation(Value.class);
            boolean hasAnnotation = value != null;

            
            try {
                // by default if:
                // - value annotation missing > source of value is port by default
                // - value annotation present > source of value is param by default
                if (hasAnnotation && value.source().equals(PARAM)) {
                    continue;

                } else {
                    var portDef = PortDefMappingUtils.fromParameter(parameter, argIndex, INPUT);
                    result.add(portDef);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException(
                        "INPUT of block definition unknown: "
                        + method.getClass().getSimpleName() + "."
                        + method.getName() + "() "
                        + parameter.getName(), e);
            }

            argIndex++;
        }
        return result;
    }



}

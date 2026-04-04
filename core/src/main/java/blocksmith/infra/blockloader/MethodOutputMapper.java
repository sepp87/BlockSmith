package blocksmith.infra.blockloader;

import blocksmith.infra.blockloader.extractor.SingleOutputExtractor;
import blocksmith.domain.value.Port;
import blocksmith.domain.value.PortDef;
import blocksmith.infra.blockloader.annotations.Display;
import blocksmith.domain.value.AutoConnectable;
import static blocksmith.domain.value.Port.Direction.OUTPUT;
import java.lang.reflect.Method;
import blocksmith.infra.blockloader.annotations.Value;
import blocksmith.infra.blockloader.extractor.RecordOutputExtractor;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author joost
 */
public class MethodOutputMapper {

    private static final String DEFAULT_OUPUT_VALUE_ID = "value";

    public static OutputMapping map(Method method) throws ReflectiveOperationException {
        Class<?> returnType = method.getReturnType();

        if (returnType.isRecord()) {
            return mapRecord(method);
        }
        return mapSingle(method);
    }

    private static OutputMapping mapRecord(Method method) {
        Class<?> returnType = method.getReturnType();
        var ports = new ArrayList<PortDef>();
        var getters = new HashMap<String, MethodHandle>();

        int argIndex = 0;
        for (var component : returnType.getRecordComponents()) {

            var portDef = PortDefMappingUtils.fromComponent(component, argIndex, OUTPUT);
            ports.add(portDef);

            try {
                var valueId = portDef.valueId();
                var handle = MethodHandles.lookup().unreflect(component.getAccessor());
                getters.put(valueId, handle);

            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            argIndex++;
        }
        var extractor = new RecordOutputExtractor(getters);
        return new OutputMapping(ports, extractor);
    }

    private static OutputMapping mapSingle(Method method) {
        var portDef = PortDefMappingUtils.fromReturnType(method, OUTPUT, DEFAULT_OUPUT_VALUE_ID);
        var valueId = portDef.valueId();
        var extractor = new SingleOutputExtractor(valueId);
        return new OutputMapping(List.of(portDef), extractor);
    }

}

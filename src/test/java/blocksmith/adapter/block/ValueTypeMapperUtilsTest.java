package blocksmith.adapter.block;

import blocksmith.domain.block.ValueType.*;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 *
 * @author joost
 */
public class ValueTypeMapperUtilsTest {

    private static final Map<String, Method> staticMethods = new HashMap<>();

    @BeforeAll
    public static void loadStaticMethods() {
        var methods = MethodLoaderUtils.getStaticMethodsFromClasses(List.of(ValueTypeMapperUtilsTest.class));
        methods.forEach(m -> staticMethods.put(m.getName(), m));
    }

    public static void string(String simple) {

    }

    public static void plainList(List list) {

    }

    public static void wildcardList(List<?> listOfWildcards) {

    }

    public static void stringList(List<String> listOfStrings) {

    }

    public static <T> void tList(List<T> listOfVarType) {

    }

    public static <T> void t(T value) {

    }

    @Test
    public void testValueType_WhenString_ThenSimpleTypeOfString() {
        System.out.println("testValueType_WhenString_ThenSimpleTypeOfString");
        var parameter = staticMethods.get("string").getParameters()[0];
        var type = ValueTypeMappingUtils.fromMethodParameter(parameter);
        Assertions.assertTrue(type instanceof SimpleType st && st.raw() == String.class);
    }

    @Test
    public void testValueType_WhenPlainList_ThenListTypeWithSimpleTypeOfObject() {
        System.out.println("testValueType_WhenPlainList_ThenListTypeWithSimpleTypeOfObject");
        var parameter = staticMethods.get("plainList").getParameters()[0];
        var type = ValueTypeMappingUtils.fromMethodParameter(parameter);
        Assertions.assertTrue(type instanceof ListType lt && lt.elementType() instanceof SimpleType st && st.raw() == Object.class);
    }

    @Test
    public void testValueType_WhenWildcardList_ThenListTypeWithSimpleTypeOfObject() {
        System.out.println("testValueType_WhenWildcardList_ThenListTypeWithSimpleTypeOfObject");
        var parameter = staticMethods.get("wildcardList").getParameters()[0];
        var type = ValueTypeMappingUtils.fromMethodParameter(parameter);
        Assertions.assertTrue(type instanceof ListType lt && lt.elementType() instanceof SimpleType st && st.raw() == Object.class);
    }

    @Test
    public void testValueType_WhenStringList_ThenListTypeWithSimpleTypeOfString() {
        System.out.println("testValueType_WhenStringList_ThenListTypeWithSimpleTypeOfString");
        var parameter = staticMethods.get("stringList").getParameters()[0];
        var type = ValueTypeMappingUtils.fromMethodParameter(parameter);
        Assertions.assertTrue(type instanceof ListType lt && lt.elementType() instanceof SimpleType st && st.raw() == String.class);
    }

    @Test
    public void testValueType_WhenTList_ThenListTypeWithVarTypeOfT() {
        System.out.println("testValueType_WhenTList_ThenListTypeWithVarTypeOfT");
        var parameter = staticMethods.get("tList").getParameters()[0];
        var type = ValueTypeMappingUtils.fromMethodParameter(parameter);
        Assertions.assertTrue(type instanceof ListType lt && lt.elementType() instanceof VarType vt && vt.name().equals("T"));
    }

    @Test
    public void testValueType_WhenT_ThenVarTypeOfT() {
        System.out.println("testValueType_WhenT_ThenVarTypeOfT");
        var parameter = staticMethods.get("t").getParameters()[0];
        var type = ValueTypeMappingUtils.fromMethodParameter(parameter);
        Assertions.assertTrue(type instanceof VarType vt && vt.name().equals("T"));
    }

}

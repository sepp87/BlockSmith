

package blocksmith.domain.value;

/**
 *
 * @author joost
 */
public sealed interface ValueDef permits PortDef, ParamDef {

    String valueId();
    int argIndex();
    String valueName();
    ValueType valueType();
    
}

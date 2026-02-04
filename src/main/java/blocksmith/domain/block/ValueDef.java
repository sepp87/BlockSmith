

package blocksmith.domain.block;

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

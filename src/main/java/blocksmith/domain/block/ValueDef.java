

package blocksmith.domain.block;

/**
 *
 * @author joost
 */
public sealed interface ValueDef permits PortDef, ParamDef {

    int argIndex();
    String name();
    ValueType valueType();
    
}

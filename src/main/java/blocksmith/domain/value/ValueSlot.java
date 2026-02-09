package blocksmith.domain.value;

/**
 *
 * @author joost
 */
public sealed interface ValueSlot permits Port, Param {

    String valueId();
        
    Object value();
    
    ValueType valueType();
    
}

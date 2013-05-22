package cz.zcu.kiv.crce.metadata;

import java.io.Serializable;
import javax.annotation.Nonnull;

/**
 *
 * @param <T>
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface Attribute<T> extends Serializable {

    @Nonnull
    T getValue();

    @Nonnull
    String getStringValue();

    @Nonnull
    AttributeType<T> getAttributeType();
    
    void setOperator(@Nonnull Operator operator);

    @Nonnull
    Operator getOperator();
}

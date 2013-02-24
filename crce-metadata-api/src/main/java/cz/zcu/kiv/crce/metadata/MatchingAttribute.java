package cz.zcu.kiv.crce.metadata;

import javax.annotation.Nonnull;

/**
 *
 * @param <T> 
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface MatchingAttribute<T> extends Attribute<T> {

    void setOperator(@Nonnull Operator operator);
    
    @Nonnull
    Operator getOperator();
}

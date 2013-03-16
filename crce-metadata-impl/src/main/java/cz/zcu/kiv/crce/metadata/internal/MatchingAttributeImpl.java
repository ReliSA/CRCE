package cz.zcu.kiv.crce.metadata.internal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.MatchingAttribute;
import cz.zcu.kiv.crce.metadata.Operator;

/**
 *
 * @param <T>
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class MatchingAttributeImpl<T> extends AttributeImpl<T> implements MatchingAttribute<T> {

    private Operator operator;

    public MatchingAttributeImpl(@Nonnull AttributeType<T> type, @Nullable T value, @Nonnull Operator operator) {
        super(type, value);
        this.operator = operator;
    }

    @Override
    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    @Override
    public Operator getOperator() {
        return operator;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MatchingAttributeImpl<T> other = (MatchingAttributeImpl<T>) obj;
        if (this.operator != other.operator) {
            return false;
        }
        return super.equals(obj);
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.operator != null ? this.operator.hashCode() : 0);
        return 97 * hash + super.hashCode();
    }
}

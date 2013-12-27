package cz.zcu.kiv.crce.metadata.dao;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import cz.zcu.kiv.crce.metadata.Attribute;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class ResourceDAOFilter {

    public static enum Operator {
        AND,
        OR
    }

    private final String namespace;
    private List<Attribute<?>> attributes = Collections.emptyList();
    private Operator operator = Operator.AND;

    public ResourceDAOFilter(@Nonnull String namespace) {
        this.namespace = namespace;
    }

    @Nonnull
    public String getNamespace() {
        return namespace;
    }

    @Nonnull
    public List<Attribute<?>> getAttributes() {
        return attributes;
    }

    public void setAttributes(@Nonnull List<Attribute<?>> attributes) {
        this.attributes = attributes;
    }

    @Nonnull
    public Operator getOperator() {
        return operator;
    }

    public void setOperator(@Nonnull Operator operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        return "{" + "namespace=" + namespace + ", attributes=" + attributes + ", operator=" + operator + '}';
    }
}

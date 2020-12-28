package cz.zcu.kiv.crce.metadata.dao.filter;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import cz.zcu.kiv.crce.metadata.Attribute;

/**
 * Date: 10.3.16
 *
 * @author Jakub Danek
 * @since 3.0.0
 * @version 3.0.0
 */
public class CapabilityFilter {

    /**
     * Operator used to join attribute constraints
     */
    private Operator operator;

    private final String namespace;
    /**
     * Attributes are joined using #operator value
     */
    private final List<Attribute<?>> attributes;
    /**
     * Sub filters are always joined using AND
     */
    private final List<CapabilityFilter> subFilters;

    public CapabilityFilter(String namespace) {
        this.namespace = namespace;

        this.attributes = new LinkedList<>();
        this.subFilters = new LinkedList<>();
    }

    public void addAttribute(Attribute<?> attribute) {
        this.attributes.add(attribute);
    }

    public void addAttributes(Collection<Attribute<?>> attributes) {
        this.attributes.addAll(attributes);
    }

    public List<Attribute<?>> getAttributes() {
        return attributes;
    }

    public void addSubFilter(CapabilityFilter subFilter) {
        this.subFilters.add(subFilter);
    }

    public void addSubFilters(Collection<CapabilityFilter> subFilters) {
        this.subFilters.addAll(subFilters);
    }

    public List<CapabilityFilter> getSubFilters() {
        return subFilters;
    }

    public String getNamespace() {
        return namespace;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CapabilityFilter)) {
            return false;
        }

        CapabilityFilter that = (CapabilityFilter) o;

        if (namespace != null ? !namespace.equals(that.namespace) : that.namespace != null) {
            return false;
        }
        if (!attributes.equals(that.attributes)) {
            return false;
        }
        return subFilters.equals(that.subFilters);

    }

    @Override
    public int hashCode() {
        int result = namespace != null ? namespace.hashCode() : 0;
        result = 31 * result + attributes.hashCode();
        result = 31 * result + subFilters.hashCode();
        return result;
    }
}

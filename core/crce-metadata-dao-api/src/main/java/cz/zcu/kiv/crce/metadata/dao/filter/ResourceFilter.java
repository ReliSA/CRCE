package cz.zcu.kiv.crce.metadata.dao.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 10.3.16
 *
 * @author Jakub Danek
 * @since 3.0.0
 * @version 3.0.0
 */
public class ResourceFilter {

    /**
     * Operator to be used to joing the filters
     */
    private Operator operator;
    private final List<CapabilityFilter> capabilityFilters;

    public ResourceFilter() {
        this.capabilityFilters = new ArrayList<>();
    }

    public void addCapabilityFilter(CapabilityFilter filter) {
        this.capabilityFilters.add(filter);
    }

    public List<CapabilityFilter> getCapabilityFilters() {
        return capabilityFilters;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ResourceDAOFilter{");
        sb.append("operator=").append(operator);
        sb.append(", capabilityFilters=").append(capabilityFilters);
        sb.append('}');
        return sb.toString();
    }
}

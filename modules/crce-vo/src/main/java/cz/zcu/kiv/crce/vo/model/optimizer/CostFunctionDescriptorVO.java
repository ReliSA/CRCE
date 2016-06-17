package cz.zcu.kiv.crce.vo.model.optimizer;

import java.io.Serializable;

/**
 * Date: 17.6.16
 *
 * @author Jakub Danek
 */
public class CostFunctionDescriptorVO implements Serializable {

    private String id;
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CostFunctionDescriptorVO)) return false;

        CostFunctionDescriptorVO that = (CostFunctionDescriptorVO) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

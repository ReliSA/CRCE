package cz.zcu.kiv.crce.vo.model.optimizer;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * Date: 17.6.16
 *
 * @author Jakub Danek
 */
@XmlRootElement(name = "optimizer-method")
public class ResultOptimizerVO implements Serializable {

    private String id;
    private String description;

    @XmlAttribute(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlValue
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResultOptimizerVO)) return false;

        ResultOptimizerVO that = (ResultOptimizerVO) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

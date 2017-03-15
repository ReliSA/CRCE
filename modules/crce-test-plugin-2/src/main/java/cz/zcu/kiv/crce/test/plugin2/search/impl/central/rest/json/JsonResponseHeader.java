package cz.zcu.kiv.crce.test.plugin2.search.impl.central.rest.json;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Map;

/**
 * This class represents responseHeader element in the json returned by
 * maven repo.
 *
 * @author Zdenek Vales
 */
@XmlRootElement
public class JsonResponseHeader implements Serializable{

    @XmlElement
    private int status;
    @XmlElement
    private int QTime;
    @XmlElement
    private Map<String, String> params;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getQTime() {
        return QTime;
    }

    public void setQTime(int QTime) {
        this.QTime = QTime;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}

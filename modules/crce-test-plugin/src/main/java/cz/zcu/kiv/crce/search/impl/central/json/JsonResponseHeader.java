package cz.zcu.kiv.crce.search.impl.central.json;

import java.util.Map;

/**
 * This class represents responseHeader element in the json returned by
 * maven repo.
 *
 * @author Zdenek Vales
 */
public class JsonResponseHeader {

    private int status;
    private int QTime;
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

package cz.zcu.kiv.crce.mvn.plugin.search.impl.central.rest.json;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Json obtained from the central maven repo will have this structure.
 *
 * @author Zdenek Vales
 */
@XmlRootElement
public class CentralRepoJsonResponse implements Serializable {

    @XmlElement
    private JsonResponseHeader responseHeader;

    @XmlElement
    private JsonResponseBody response;

    public JsonResponseHeader getResponseHeader() {
        return responseHeader;
    }

    public void setResponseHeader(JsonResponseHeader responseHeader) {
        this.responseHeader = responseHeader;
    }

    public JsonResponseBody getResponse() {
        return response;
    }

    public void setResponse(JsonResponseBody response) {
        this.response = response;
    }
}

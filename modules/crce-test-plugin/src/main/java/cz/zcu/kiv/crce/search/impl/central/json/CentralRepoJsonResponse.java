package cz.zcu.kiv.crce.search.impl.central.json;

/**
 * Json obtained from the central maven repo will have this structure.
 *
 * @author Zdenek Vales
 */
public class CentralRepoJsonResponse {

    private JsonResponseHeader responseHeader;

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

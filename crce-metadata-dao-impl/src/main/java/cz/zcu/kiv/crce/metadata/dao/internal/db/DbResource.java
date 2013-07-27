package cz.zcu.kiv.crce.metadata.dao.internal.db;

/**
 *
 * @author cihlator
 */
public class DbResource {

    private long resourceId;
    private String id;
    private String uri;

    public long getResourceId() {
        return resourceId;
    }

    public void setResourceId(long resourceId) {
        this.resourceId = resourceId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}

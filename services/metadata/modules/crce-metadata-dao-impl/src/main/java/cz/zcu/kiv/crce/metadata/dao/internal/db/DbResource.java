package cz.zcu.kiv.crce.metadata.dao.internal.db;

/**
 *
 * @author cihlator
 */
public class DbResource {

    private long resourceId;
    private long repositoryId;
    private String repositoryUuid;
    private String id;
    private String uri;

    public long getResourceId() {
        return resourceId;
    }

    public void setResourceId(long resourceId) {
        this.resourceId = resourceId;
    }

    public long getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(long repositoryId) {
        this.repositoryId = repositoryId;
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

    public String getRepositoryUuid() {
        return repositoryUuid;
    }

    public void setRepositoryUuid(String repositoryUuid) {
        this.repositoryUuid = repositoryUuid;
    }
}

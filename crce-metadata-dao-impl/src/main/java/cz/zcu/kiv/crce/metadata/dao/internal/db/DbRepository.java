package cz.zcu.kiv.crce.metadata.dao.internal.db;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class DbRepository {

    private long repositoryId;
    private String id;
    private String uri;

    public long getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(long repositoryId) {
        this.repositoryId = repositoryId;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

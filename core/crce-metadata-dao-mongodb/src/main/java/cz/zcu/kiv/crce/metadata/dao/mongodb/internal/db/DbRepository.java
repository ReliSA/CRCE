package cz.zcu.kiv.crce.metadata.dao.mongodb.internal.db;

import org.mongojack.Id;
import org.mongojack.MongoCollection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@MongoCollection(name = "repositories")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DbRepository {

    private String id;
    private String uri;


    @JsonProperty("uri")
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Id
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbRepository)) return false;

        DbRepository that = (DbRepository) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

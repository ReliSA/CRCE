package cz.zcu.kiv.crce.metadata.dao.mongodb.internal.db;

import java.util.Set;

import org.mongojack.Id;
import org.mongojack.MongoCollection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@MongoCollection(name = "properties")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DbProperty {

    private String id;
    private String namespace;
    private String resourceId;

    private Set<DbAttribute<?>> attributes;

    @JsonProperty("id")
    @Id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty(DbResource.RESOURCE_ID)
    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    @JsonProperty("namespace")
    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @JsonProperty("attributes")
    public Set<DbAttribute<?>> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<DbAttribute<?>> attributes) {
        this.attributes = attributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbProperty)) return false;

        DbProperty that = (DbProperty) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

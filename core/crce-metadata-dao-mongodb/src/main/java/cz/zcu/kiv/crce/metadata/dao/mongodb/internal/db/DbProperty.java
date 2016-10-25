package cz.zcu.kiv.crce.metadata.dao.mongodb.internal.db;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class DbProperty {

    private String id;
    private String namespace;

    private Set<DbAttribute<?>> attributes;

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

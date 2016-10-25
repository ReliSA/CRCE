package cz.zcu.kiv.crce.metadata.dao.mongodb.internal.db;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Cihlář
 */
public class DbRequirement {

    private String id;
    private String namespace;

    private Set<DbRequirement> children;
    private Set<DbAttribute> attributes;
    private Set<DbDirective> directives;

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

    @JsonProperty("children")
    public Set<DbRequirement> getChildren() {
        return children;
    }

    public void setChildren(Set<DbRequirement> children) {
        this.children = children;
    }

    @JsonProperty("attributes")
    public Set<DbAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<DbAttribute> attributes) {
        this.attributes = attributes;
    }

    @JsonProperty("directives")
    public Set<DbDirective> getDirectives() {
        return directives;
    }

    public void setDirectives(Set<DbDirective> directives) {
        this.directives = directives;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbRequirement)) return false;

        DbRequirement that = (DbRequirement) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

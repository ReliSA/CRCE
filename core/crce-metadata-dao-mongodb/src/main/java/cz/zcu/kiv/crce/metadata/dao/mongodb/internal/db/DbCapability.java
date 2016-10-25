package cz.zcu.kiv.crce.metadata.dao.mongodb.internal.db;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Cihlář
 */
public class DbCapability {

    public static final String NAMESPACE = "namespace";
    public static final String ATTRIBUTES = "attributes";
    public static final String CHILDREN = "children";

    private String id;
    private String namespace;

    private Set<DbAttribute<?>> attributes;
    private Set<DbDirective> directives;
    private Set<DbProperty> properties;
    private Set<DbRequirement> requirements;
    private Set<DbCapability> children;

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty(NAMESPACE)
    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @JsonProperty(ATTRIBUTES)
    public Set<DbAttribute<?>> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<DbAttribute<?>> attributes) {
        this.attributes = attributes;
    }

    @JsonProperty("directives")
    public Set<DbDirective> getDirectives() {
        return directives;
    }

    public void setDirectives(Set<DbDirective> directives) {
        this.directives = directives;
    }

    @JsonProperty("properties")
    public Set<DbProperty> getProperties() {
        return properties;
    }

    public void setProperties(Set<DbProperty> properties) {
        this.properties = properties;
    }

    @JsonProperty("requirements")
    public Set<DbRequirement> getRequirements() {
        return requirements;
    }

    public void setRequirements(Set<DbRequirement> requirements) {
        this.requirements = requirements;
    }

    @JsonProperty(CHILDREN)
    public Set<DbCapability> getChildren() {
        return children;
    }

    public void setChildren(Set<DbCapability> children) {
        this.children = children;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbCapability)) return false;

        DbCapability that = (DbCapability) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

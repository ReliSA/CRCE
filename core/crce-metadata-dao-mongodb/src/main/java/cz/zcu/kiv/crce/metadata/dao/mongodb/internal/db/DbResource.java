package cz.zcu.kiv.crce.metadata.dao.mongodb.internal.db;

import java.util.Set;

import org.mongojack.Id;
import org.mongojack.MongoCollection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author cihlator
 */
@MongoCollection(name = "resources")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DbResource {

    public static final String CAPABILITIES = "capabilities";
    public static final String REPOSITORY_UUID = "repository_id";
    public static final String URI = "uri";

    private String repositoryUuid;
    private String id;
    private String uri;

    private Set<DbCapability> capabilities;
    private Set<DbRequirement> requirements;
    private Set<DbProperty> properties;


    @Id
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty(URI)
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @JsonProperty(REPOSITORY_UUID)
    public String getRepositoryUuid() {
        return repositoryUuid;
    }

    public void setRepositoryUuid(String repositoryUuid) {
        this.repositoryUuid = repositoryUuid;
    }

    @JsonProperty(CAPABILITIES)
    public Set<DbCapability> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(Set<DbCapability> capabilities) {
        this.capabilities = capabilities;
    }

    @JsonProperty("requirements")
    public Set<DbRequirement> getRequirements() {
        return requirements;
    }

    public void setRequirements(Set<DbRequirement> requirements) {
        this.requirements = requirements;
    }

    @JsonProperty("properties")
    public Set<DbProperty> getProperties() {
        return properties;
    }

    public void setProperties(Set<DbProperty> properties) {
        this.properties = properties;
    }
}

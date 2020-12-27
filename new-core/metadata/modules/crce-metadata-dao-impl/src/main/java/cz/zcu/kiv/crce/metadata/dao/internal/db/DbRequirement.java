package cz.zcu.kiv.crce.metadata.dao.internal.db;

/**
 *
 * @author Cihlář
 */
public class DbRequirement {

    private long requirementId;
    private long parentRequirementId;
    private long resourceId;
    private Long capabilityId;
    private int level;
    private String id;
    private String namespace;

    public long getRequirementId() {
        return requirementId;
    }

    public void setRequirementId(long requirementId) {
        this.requirementId = requirementId;
    }

    public long getParentRequirementId() {
        return parentRequirementId;
    }

    public void setParentRequirementId(long parentRequirementId) {
        this.parentRequirementId = parentRequirementId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public long getResourceId() {
        return resourceId;
    }

    public void setResourceId(long resourceId) {
        this.resourceId = resourceId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Long getCapabilityId() {
        return capabilityId;
    }

    public void setCapabilityId(Long capabilityId) {
        this.capabilityId = capabilityId;
    }
}

package cz.zcu.kiv.crce.metadata.dao.internal.db;

/**
 *
 * @author Cihlář
 */
public class DbCapability {

    private long capabilityId;
    private long parentCapabilityId;
    private long resourceId;
    private String id;
    private String namespace;

    public long getCapabilityId() {
        return capabilityId;
    }

    public void setCapabilityId(long capabilityId) {
        this.capabilityId = capabilityId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getParentCapabilityId() {
        return parentCapabilityId;
    }

    public void setParentCapabilityId(long parentCapabilityId) {
        this.parentCapabilityId = parentCapabilityId;
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
}

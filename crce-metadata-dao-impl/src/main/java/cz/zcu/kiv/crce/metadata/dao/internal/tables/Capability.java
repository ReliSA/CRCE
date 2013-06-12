
package cz.zcu.kiv.crce.metadata.dao.internal.tables;

/**
 *
 * @author Cihlář
 */
public class Capability {
    //private int internal_id; // not needed - autoincrement
    private String id;
    private String namespace;
    private int capability_id;
    private int resource_id;

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

    public int getCapability_id() {
        return capability_id;
    }

    public void setCapability_id(int capability_id) {
        this.capability_id = capability_id;
    }

    public int getResource_id() {
        return resource_id;
    }

    public void setResource_id(int resource_id) {
        this.resource_id = resource_id;
    }
    
}

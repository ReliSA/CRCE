
package cz.zcu.kiv.crce.metadata.dao.internal;

/**
 *
 * @author Cihlář
 */
public class Requirement {
    
    //private int internal_id; // not needed - autoincrement
    private String id;
    private String namespace;
    private int requirement_id;
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

    public int getRequirement_id() {
        return requirement_id;
    }

    public void setRequirement_id(int requirement_id) {
        this.requirement_id = requirement_id;
    }

    public int getResource_id() {
        return resource_id;
    }

    public void setResource_id(int resource_id) {
        this.resource_id = resource_id;
    }
    

}

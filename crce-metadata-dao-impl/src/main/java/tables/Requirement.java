
package tables;

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
        return this.id;
    }   
    public String getNamespace() {
        return this.namespace;
    }
    public int getRequirement_id() {
        return this.requirement_id;
    }   
    public int getResource_id() {
        return this.resource_id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public void setNamespace(String name) {
        this.namespace = name;
    }
    public void setRequirement_id(int requirement_id) {
        this.requirement_id = requirement_id;
    }
    public void setNamespace(int resource_id) {
        this.resource_id = resource_id;
    }
}

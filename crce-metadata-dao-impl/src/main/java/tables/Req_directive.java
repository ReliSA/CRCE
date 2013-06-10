
package tables;

/**
 *
 * @author Cihlář
 */
public class Req_directive {
    // private int internal id; // not needed
    private String name;
    private String value;
    private int requirement_id;

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public int getRequirement_id() {
        return requirement_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setRequirement_id(int requirement_id) {
        this.requirement_id = requirement_id;
    }
    
}

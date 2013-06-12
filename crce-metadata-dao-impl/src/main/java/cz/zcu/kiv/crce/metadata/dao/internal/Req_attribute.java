
package cz.zcu.kiv.crce.metadata.dao.internal;

/**
 *
 * @author Cihlář
 */
public class Req_attribute {
    //private int internal_id; // not needed
    private String name;
    private String type;
    private String value;
    private String operator;
    private int requirement_id;

    public int getRequirement_id() {
        return requirement_id;
    }

    public void setRequirement_id(int requirement_id) {
        this.requirement_id = requirement_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
    
}

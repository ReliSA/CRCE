
package cz.zcu.kiv.crce.metadata.dao.internal.tables;

/**
 *
 * @author Cihlář
 */
public class Cap_attribute {
    //private int internal_id; // not needed
    private String name;
    private String type;
    private String value;
    private String operator;
    private int capability_id;

    public int getCapability_id() {
        return capability_id;
    }

    public void setCapability_id(int capability_id) {
        this.capability_id = capability_id;
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

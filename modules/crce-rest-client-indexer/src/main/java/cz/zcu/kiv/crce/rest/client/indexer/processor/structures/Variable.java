package cz.zcu.kiv.crce.rest.client.indexer.processor.structures;

import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.ClassTools;

public class Variable {

    public enum VariableType {
        SIMPLE, OTHER, ENDPOINT, ARRAY, ENDPOINT_DATA, WS_CLIENT_DATA, MAP
    }

    private Object value = "";
    private String owner = "";
    private String description = "";
    private VariableType vType = null;


    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /**
     * Description is type of the variable java/lang/String etc.
     * @param description
     * @return this
     */
    public Variable setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * 
     * @return Descriptions
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * If variable is type of SIMPLE it can be easily stringified
     * available type: SIMPLE, OTHER, ENDPOINT, ARRAY, ENDPOINTDATA
     * @param vType Variable type
     * @return this
     */
    public Variable setType(VariableType vType) {
        this.vType = vType;
        return this;
    }

    /**
     * Build Variable type from given description
     * @param description Type e.g. java/lang/String
     * @return this
     */
    public Variable setType(String description) {
        if (ClassTools.isPrimitive(description)) {
            this.vType = VariableType.SIMPLE;
        } else {
            this.vType = VariableType.OTHER;
        }
        return this;
    }

    /**
     * 
     * @return Variable type
     */
    public VariableType getType() {
        return this.vType;
    }

    /**
     * @param value the value to set
     */
    public Variable setValue(Object value) {
        this.value = value;
        return this;
    }

    /**
     * @param value
     */
    public Variable(Object value) {
        this.value = value;
    }

    public Variable() {
        this.value = "";
    }

    /**
     * Owner is source class of method
     * @param owner
     * @return
     */
    public Variable setOwner(String owner) {
        this.owner = owner;
        return this;
    }

    /**
     * 
     * @return Owner
     */
    public String getOwner() {
        return this.owner;
    }

    /**
     * To string ...
     */
    @Override
    public String toString() {
        return value != null ? value.toString() : "";
    }

    /**
     * Adds value into variable (this)
     * @param newString New value
     */
    public void add(String newString) {
        this.value = value.toString() + newString;
    }

    /**
     * Processes Variable and save into this variable
     * @param var Incoming Variable
     */
    public void add(Variable var) {
        this.value = value.toString() + var.toString();
    }
}

package cz.zcu.kiv.crce.rest.client.indexer.processor;

import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.ClassTools;

public class Variable {

    public enum VariableType {
        SIMPLE, OTHER, ENDPOINT, ARRAY, ENDPOINTDATA
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

    public Variable setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getDescription() {
        return this.description;
    }

    public Variable setType(VariableType vType) {
        this.vType = vType;
        return this;
    }

    public Variable setType(String description) {
        if (ClassTools.isPrimitive(description)) {
            this.vType = VariableType.SIMPLE;
        } else {
            this.vType = VariableType.OTHER;
        }
        return this;
    }

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

    public Variable setOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public String getOwner() {
        return this.owner;
    }

    @Override
    public String toString() {
        return value != null ? value.toString() : "";
    }

    public void add(String newString) {
        this.value = value.toString() + newString;
    }

    public void add(Variable var) {
        this.value = value.toString() + var.toString();
    }

}

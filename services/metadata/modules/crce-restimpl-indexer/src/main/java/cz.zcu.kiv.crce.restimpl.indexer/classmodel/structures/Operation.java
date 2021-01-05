package cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures;


import cz.zcu.kiv.crce.restimpl.indexer.classmodel.extracting.BytecodeDescriptorsProcessor;

/**
 * Created by ghessova on 08.04.2018.
 */
public class Operation {

    private int opcode;
    private OperationType type;
    private Object value;
    private String dataType;
    private int index;
    private String owner;
    private String name;
    private String desc;

    private String description;

    public Operation(int opcode, OperationType type) {
        this.opcode = opcode;
        this.type = type;
    }

    public Operation(OperationType type) {
        this.type = type;
    }

    public OperationType getType() {
        return type;
    }

    public void setType(OperationType type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        if (type == OperationType.CALL) {
            this.setDataType(BytecodeDescriptorsProcessor.processMethodDescriptor(desc).getReturnType().getBasicType());
        }
        this.desc = desc;
    }

    public enum OperationType {

        STORE, LOAD, STRING_CONSTANT, INT_CONSTANT, JUMP, RETURN, CALL, OTHER, FIELD;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    @Override
    public String toString() {
        return description + ": " + type + "(opcode=" + opcode + "), " + (desc == null ? "" : desc);
    }

    public int getOpcode() {
        return opcode;
    }

    public void setOpcode(int opcode) {
        this.opcode = opcode;
    }
}

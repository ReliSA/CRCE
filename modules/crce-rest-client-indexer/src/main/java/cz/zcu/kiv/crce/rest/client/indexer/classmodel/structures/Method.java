package cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures;

import java.util.ArrayList;
import java.util.List;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.extracting.BytecodeDescriptorsProcessor;

/**
 * Inspired by ghessova on 05.03.2018.
 */
public class Method {

    private int access;
    private String desc;
    private DataType returnType;
    private String name;
    private String owner;
    private String description;
    private List<Variable> parameters;
    private List<List<String>> responsesLog = new ArrayList<>();
    private List<Operation> bodyLog = new ArrayList<>();

    private String returnValue = "";

    public Method(int access, String name, String desc, String owner) {
        this.access = access;
        this.name = name;
        this.desc = desc;
        BytecodeDescriptorsProcessor.processMethodDescriptor(desc, this);
        this.owner = owner;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return Name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @return Return Type
     */
    public DataType getReturnType() {
        return returnType;
    }

    /**
     * Sets return type
     * @param returnType Return type
     */
    public void setReturnType(DataType returnType) {
        this.returnType = returnType;
    }

    /**
     * @return List of parameters
     */
    public List<Variable> getParameters() {
        return parameters;
    }

    /**
     * Sets parameters
     * @param parameters Parameters
     */
    public void setParameters(List<Variable> parameters) {
        this.parameters = parameters;
    }

    /**
     * Logging
     * @param log
     */
    public void addLog(List<String> log) {
        responsesLog.add(log);
    }

    /**
     * Response log
     * @return
     */
    public List<List<String>> getResponsesLog() {
        return responsesLog;
    }

    /**
     *
     * @return Operations within scope of this method
     */
    public List<Operation> getOperations() {
        return bodyLog;
    }

    /**
     * Adds operation into bodyLog 
     * @param operation New operation
     */
    public void addOperation(Operation operation) {
        bodyLog.add(operation);
    }

    /**
     * Sets method access
     * @param access Acccess
     */
    public void setAccess(int access) {
        this.access = access;
    }

    /**
     * 
     * @return Method access
     */
    public int getAccess() {
        return access;
    }

    /**
     * 
     * @return Description of method e.g. Is it array (Ljava/lang/String;)V
     */
    public String getDesc() {
        return desc;
    }

    public String getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return "Method{" + "name='" + name + '\'' + ", returnType='" + returnType + '\''
                + " returnValue=" + returnValue + '}';
    }

    /**
     * @return the returnValue
     */
    public String getReturnValue() {
        return returnValue;
    }

    /**
     * @param returnValue the returnValue to set
     */
    public void setReturnValue(String returnValue) {
        this.returnValue = returnValue;
    }
}

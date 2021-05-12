package cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures;

import java.util.ArrayList;
import java.util.List;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.extracting.BytecodeDescriptorsProcessor;

/**
 * Created by ghessova on 05.03.2018.
 */
public class Method extends PathPart {

    private int access;
    private String desc;
    private String signature;
    private DataType returnType;
    private String owner;
    private String[] exceptions;
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

    public String getName() {
        return name;
    }

    public DataType getReturnType() {
        return returnType;
    }

    public void setReturnType(DataType returnType) {
        this.returnType = returnType;
    }

    public List<Variable> getParameters() {
        return parameters;
    }

    public void setParameters(List<Variable> parameters) {
        this.parameters = parameters;
    }

    public void addLog(List<String> log) {
        responsesLog.add(log);
    }

    public List<List<String>> getResponsesLog() {
        return responsesLog;
    }

    public List<Operation> getOperations() {
        return bodyLog;
    }

    public void addOperation(Operation operation) {
        bodyLog.add(operation);
    }

    public void setAccess(int access) {
        this.access = access;
    }

    public int getAccess() {
        return access;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String[] getExceptions() {
        return exceptions;
    }

    public void setExceptions(String[] exceptions) {
        this.exceptions = exceptions;
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

    /**
     * @return the signature
     */
    public String getSignature() {
        return signature;
    }

    /**
     * @param signature the signature to set
     */
    public void setSignature(String signature) {
        this.signature = signature;
    }
}

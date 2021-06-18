package cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ghessova on 05.03.2018.
 */
public class Method extends PathPart {

    private int access;
    private String desc;
    private String description;
    private DataType returnType;
    private String[] exceptions;
    private List<Variable> parameters;
    private List<List<String>> responsesLog = new ArrayList<>();
    private List<Operation> bodyLog = new ArrayList<>();

    public Method(int access, String name, String desc) {
        this.access = access;
        this.name = name;
        this.desc = desc;
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

    public List<Operation> getBodyLog() {
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

    @Override
    public String toString() {
        return "Method{" + "name='" + name + '\'' + ", returnType='" + returnType + '\'' + '}';
    }
}

package cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures;

import java.util.List;

/**
 * Created by ghessova on 25.04.2018.
 */
public class MethodSignature {

    private DataType returnType;
    private List<DataType> parameterTypes;

    public MethodSignature(DataType returnType, List<DataType> parameterTypes) {
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
    }

    public DataType getReturnType() {
        return returnType;
    }

    public void setReturnType(DataType returnType) {
        this.returnType = returnType;
    }

    public List<DataType> getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(List<DataType> parameterTypes) {
        this.parameterTypes = parameterTypes;
    }
}

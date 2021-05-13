package cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures;

import java.util.List;

/**
 * Created by ghessova on 25.04.2018.
 */
public class MethodSignature {

    private DataType returnType;
    private List<DataType> parameterTypes;

    /**
     * 
     * @param returnType Return type
     * @param parameterTypes Parameter Type
     */
    public MethodSignature(DataType returnType, List<DataType> parameterTypes) {
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
    }

    /**
     * 
     * @return DataType
     */
    public DataType getReturnType() {
        return returnType;
    }

    /**
     * Sets return type
     * @param returnType Return Type
     */
    public void setReturnType(DataType returnType) {
        this.returnType = returnType;
    }

    /**
     * 
     * @return Parameters types
     */
    public List<DataType> getParameterTypes() {
        return parameterTypes;
    }

    /**
     * Sets parameter types
     * @param parameterTypes Parameter types
     */
    public void setParameterTypes(List<DataType> parameterTypes) {
        this.parameterTypes = parameterTypes;
    }
}

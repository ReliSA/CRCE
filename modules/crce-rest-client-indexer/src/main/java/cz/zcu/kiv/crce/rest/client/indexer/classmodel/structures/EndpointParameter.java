package cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures;

import java.io.Serializable;
import java.util.Objects;
import cz.zcu.kiv.crce.rest.client.indexer.config.Header;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.ToStringTools;

/**
 * Created by ghessova on 10.03.2018.
 */
public class EndpointParameter implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -6837202021942397888L;

    private Header dataTypeH = null;
    private String name = "";
    private String dataType = ""; // T
    private boolean isArray = false;

    private ParameterCategory category;

    public EndpointParameter(String name, Header dataType, boolean isArray,
            ParameterCategory category) {
        this(name, null, isArray);
        this.category = category;
        this.dataTypeH = dataType;
    }

    public EndpointParameter(String name, String datatype, boolean isArray,
            ParameterCategory category) {
        this(name, datatype, isArray);
        this.category = category;
    }

    public EndpointParameter(String name, String datatype, boolean isArray) {
        this.name = name != null ? name : "";
        this.dataType = datatype;
        this.isArray = isArray;
    }

    /**
     * @return the dataTypeH
     */
    public Header getDataTypeH() {
        return dataTypeH;
    }

    /**
     * @param dataTypeH the dataTypeH to set
     */
    public void setDataTypeH(Header dataTypeH) {
        this.dataTypeH = dataTypeH;
    }

    public EndpointParameter() {

    }

    public ParameterCategory getCategory() {
        return category;
    }

    public void setCategory(ParameterCategory category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public boolean isArray() {
        return isArray;
    }

    public void setArray(boolean array) {
        isArray = array;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, dataType, isArray, category);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EndpointParameter) {
            EndpointParameter eParam = (EndpointParameter) obj;
            final boolean nameEq = name == eParam.getName() || name.equals(eParam.getName());
            final boolean dataTypeEq =
                    dataType == eParam.getDataType() || dataType.equals(eParam.getDataType());
            final boolean isArrayEq = isArray == eParam.isArray();
            final boolean categoryEq = category == eParam.getCategory();
            final boolean dataTypeHEq =
                    dataTypeH == eParam.getDataTypeH() || dataTypeH.equals(eParam.getDataTypeH());
            return nameEq && dataTypeEq && isArrayEq && categoryEq && dataTypeHEq;
        }
        return false;
    }

    public String getDataTypeS(){
        if (dataType == null || dataTypeH == null) {
            if (dataType != null && !dataType.isEmpty()) {
                return  dataType;
            } else if (dataTypeH != null) {
                return dataTypeH.toString();
            } else {
                return null;
            }
        }
        return dataType;
    }

    public String dataTypeToString(){
        String dataTypeObj = ", \"dataType\": ";
        if (dataType == null || dataTypeH == null) {
            if (dataType != null && !dataType.isEmpty()) {
                dataTypeObj += ToStringTools.objToString(dataType);
            } else if (dataTypeH != null) {
                dataTypeObj += dataTypeH;
            } else {
                dataTypeObj = "";
            }
        }
        return  dataTypeObj;
    }

    @Override
    public String toString() {
        String dateTypeObj = dataTypeToString();
        return "{" + "\"name\": " + ToStringTools.objToString(getName()) + ", \"category\": "
                + ToStringTools.objToString(getCategory()) + dateTypeObj + ", \"isArray\": "
                + isArray() + "}";
    }
}

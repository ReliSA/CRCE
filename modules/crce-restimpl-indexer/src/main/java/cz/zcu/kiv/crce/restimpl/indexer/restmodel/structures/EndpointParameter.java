package cz.zcu.kiv.crce.restimpl.indexer.restmodel.structures;

/**
 * Created by ghessova on 10.03.2018.
 */
public class EndpointParameter {

    private String name;
    private String dataType; // T
    private boolean isArray;

    private ParameterCategory category;

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
    public String toString() {
        return "EndpointParameter{" +
                "name='" + getName() + '\'' +
                ", category='" + getCategory() + '\'' +
                ", dataType='" + getDataType() + '\'' +
                ", isArray=" + isArray() +
                '}';
    }
}
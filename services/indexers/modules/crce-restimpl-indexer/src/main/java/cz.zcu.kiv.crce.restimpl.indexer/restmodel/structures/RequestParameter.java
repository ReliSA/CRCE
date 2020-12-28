package cz.zcu.kiv.crce.restimpl.indexer.restmodel.structures;

/**
 * Created by ghessova on 10.05.2018.
 */
public class RequestParameter extends EndpointParameter {

    private boolean isOptional = true;
    private String defaultValue;

    public boolean isOptional() {
        return isOptional;
    }

    public void setOptional(boolean optional) {
        isOptional = optional;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        return "RequestParameter{" +
                "name='" + getName() + '\'' +
                ", category='" + getCategory() + '\'' +
                ", dataType='" + getDataType() + '\'' +
                ", isOptional=" + isOptional() +
                ", isArray=" + isArray() +
                ", defaultValue=" + getDefaultValue() +
                '}';
    }
}


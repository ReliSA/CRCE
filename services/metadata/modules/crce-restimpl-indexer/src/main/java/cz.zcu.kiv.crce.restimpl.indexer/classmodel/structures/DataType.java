package cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures;

import java.util.Objects;

/**
 * Created by ghessova on 24.04.2018.
 */
public class DataType {

    private String basicType;
    private DataType innerType;

    public DataType(String basicType) {
        this.basicType = basicType;
    }

    public String getBasicType() {
        return basicType;
    }

    public void setBasicType(String basicType) {
        this.basicType = basicType;
    }

    public DataType getInnerType() {
        return innerType;
    }

    public void setInnerType(DataType innerType) {
        this.innerType = innerType;
    }

    public boolean isStructured() {
        return innerType != null;
    }

    @Override
    public String toString() {
        return "DataType{" +
                ", basicType='" + basicType + '\'' +
                ", innerType=" + innerType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataType dataType = (DataType) o;
        return Objects.equals(basicType, dataType.basicType) &&
                Objects.equals(innerType, dataType.innerType);
    }

    @Override
    public int hashCode() {

        return Objects.hash(basicType, innerType);
    }
}

package cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures;

/**
 * Inspired by ghessova on 10.03.2018. Field or method parameter
 */
public class Variable {

    private String name;
    private DataType dataType;

    public Variable(DataType dataType) {
        this.dataType = dataType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    @Override
    public String toString() {
        return "Variable{" + "name='" + name + '\'' + ", dataType='" + dataType + '}';
    }
}

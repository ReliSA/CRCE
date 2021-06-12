package cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures;

import java.util.Objects;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.ToJSONTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.structures.Variable;

public class Header {

    private HeaderGroup headerGroup;
    private String type;
    private String value;

    /**
     * @return the headerGroup
     */
    public HeaderGroup getHeaderGroup() {
        return headerGroup;
    }

    /**
     * @param headerGroup the headerGroup to set
     */
    public void setHeaderGroup(HeaderGroup headerGroup) {
        this.headerGroup = headerGroup;
    }

    /**
     * @return the name
     */
    public String getType() {
        return type;
    }

    /**
     * @param name the name to set
     */
    public void setType(String name) {
        this.type = name;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "{ \"name\": " + ToJSONTools.convertString(type) + ", \"value\": "
                + ToJSONTools.convertString(value) + " }";
    }

    /**
     * @param type
     * @param value
     */
    public Header(String type, String value) {
        this.type = type;
        this.value = value;
    }

    /**
    * @param type
    * @param value
    */
    public Header(String type, Variable value) {
        this.type = type;
        if (value == null) {
            this.value = "";
        } else {
            this.value =
                    value.getValue() == null ? value.getDescription() : (String) value.getValue();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Header) {
            Header header = (Header) obj;
            return header.getType().equals(header.getType())
                    && header.getValue().equals(header.getValue());
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return Objects.hash(type, value);
    }

}

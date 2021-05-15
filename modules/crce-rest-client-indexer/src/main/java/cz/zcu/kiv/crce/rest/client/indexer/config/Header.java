package cz.zcu.kiv.crce.rest.client.indexer.config;

import java.util.Objects;
import cz.zcu.kiv.crce.rest.client.indexer.processor.Variable;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.ToStringTools;

public class Header {
    private String name;
    private String value;



    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
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
        return "{ \"name\": " + ToStringTools.stringToString(name) + ", \"value\": "
                + ToStringTools.stringToString(value) + " }";
    }

    /**
     * @param name
     * @param value
     */
    public Header(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
    * @param name
    * @param value
    */
    public Header(String name, Variable value) {
        this.name = name;
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
            return header.getName().equals(header.getName())
                    && header.getValue().equals(header.getValue());
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return Objects.hash(name, value);
    }

}

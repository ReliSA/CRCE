package cz.zcu.kiv.crce.compatibility;


import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * Enumeration for specification of level which a difference value is related to.
 * <p/>
 * Package, class, operation (method or constructor) or field are meant by the level.
 * <p/>
 * Date: 13.3.14
 *
 * @author Jakub Danek
 */
@XmlType
@XmlEnum
public enum DifferenceLevel {

    @XmlEnumValue("package")
    PACKAGE("package"),
    @XmlEnumValue("type")
    TYPE("type"),
    @XmlEnumValue("operation")
    OPERATION("operation"),    //both methods and constructors
    @XmlEnumValue("field")
    FIELD("field"),
    @XmlEnumValue("unknown")
    UNKNOWN("unknown");

    private final String value;

    DifferenceLevel(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

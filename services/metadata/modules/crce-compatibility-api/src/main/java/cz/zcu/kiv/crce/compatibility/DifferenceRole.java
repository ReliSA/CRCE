package cz.zcu.kiv.crce.compatibility;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * Enumeration of possible roles of compared bundle parts.
 * <p/>
 * Date: 13.3.14
 *
 * @author Jakub Danek
 */
@XmlType
@XmlEnum
public enum DifferenceRole {
    @XmlEnumValue("CAP")
    CAPABILITY("capability"),
    @XmlEnumValue("REQ")
    REQUIREMENT("requirement");

    private final String value;

    DifferenceRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

package cz.zcu.kiv.crce.vo.model.metadata;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import cz.zcu.kiv.crce.vo.model.ValueObject;

/**
 * Date: 11.4.16
 *
 * @author Jakub Danek
 */
@XmlRootElement(name = "directive")
public class DirectiveVO extends ValueObject {

    /**
     * Directive name.
     */
    private String name;
    /**
     * Directive value.
     */
    private String value;

    public DirectiveVO() {
    }

    public DirectiveVO(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @XmlAttribute(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute(name = "value")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}

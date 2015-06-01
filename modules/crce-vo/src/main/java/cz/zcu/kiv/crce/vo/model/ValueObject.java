package cz.zcu.kiv.crce.vo.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Date: 18.5.15
 *
 * @author Jakub Danek
 */
public class ValueObject implements Serializable {

    private String id;
    private String namespace;

    public ValueObject() {
    }

    public ValueObject(String id) {
        this.id = id;
    }

    public ValueObject(String id, String namespace) {
        this.id = id;
        this.namespace = namespace;
    }

    @XmlAttribute(name = "uuid")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlAttribute(name = "namespace")
    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}

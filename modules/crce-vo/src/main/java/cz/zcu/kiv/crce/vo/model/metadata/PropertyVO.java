package cz.zcu.kiv.crce.vo.model.metadata;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import cz.zcu.kiv.crce.vo.model.ValueObject;

/**
 * Value object for {@link cz.zcu.kiv.crce.metadata.Property}
 *
 * Date: 1.6.15
 *
 * @author Jakub Danek
 */
@XmlRootElement(name = "property")
public class PropertyVO extends ValueObject {

    private List<AttributeVO> attributes = new ArrayList<>();

    public PropertyVO() {
    }

    @Nonnull
    @XmlElementRef
    public List<AttributeVO> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributeVO> attributes) {
        this.attributes = attributes;
    }
}

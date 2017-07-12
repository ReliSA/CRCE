package cz.zcu.kiv.crce.vo.model.metadata;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import cz.zcu.kiv.crce.vo.model.ValueObject;

/**
 *
 * Generic capability for {@link cz.zcu.kiv.crce.metadata.Capability}
 *
 * Date: 5.5.15
 *
 * @author Jakub Danek
 */
@XmlRootElement(name = "capability")
public class GenericCapabilityVO extends ValueObject {

    /**
     * List of capability attributes.
     */
    private List<AttributeVO> attributes = new ArrayList<>();
    /**
     * List of subcapabilities.
     */
    private List<GenericCapabilityVO> capabilities = new ArrayList<>();
    /**
     * List of capability's properties.
     */
    private List<PropertyVO> properties = new ArrayList<>();
    /**
     * List of capability's requirements.
     */
    private List<GenericRequirementVO> requirements = new ArrayList<>();


    public GenericCapabilityVO() {
    }

    public GenericCapabilityVO(String namespace) {
        this("", namespace);
    }

    public GenericCapabilityVO(String id, String namespace) {
        super(id, namespace);
    }


    @Nonnull
    @XmlElementRef
    public List<AttributeVO> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributeVO> attributes) {
        this.attributes = attributes;
    }

    @Nonnull
    @XmlElementRef
    public List<GenericCapabilityVO> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(List<GenericCapabilityVO> capabilities) {
        this.capabilities = capabilities;
    }

    @Nonnull
    @XmlElementRef
    public List<PropertyVO> getProperties() {
        return properties;
    }

    public void setProperties(List<PropertyVO> properties) {
        this.properties = properties;
    }

    @Nonnull
    @XmlElementRef
    public List<GenericRequirementVO> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<GenericRequirementVO> requirements) {
        this.requirements = requirements;
    }
}

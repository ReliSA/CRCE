package cz.zcu.kiv.crce.vo.model.metadata;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import cz.zcu.kiv.crce.vo.model.ValueObject;

/**
 * Date: 5.5.15
 *
 * @author Jakub Danek
 */
@XmlRootElement(name = "capability")
public class GenericCapabilityVO extends ValueObject {

    public GenericCapabilityVO() {
    }

    public GenericCapabilityVO(String namespace) {
        this("", namespace);
    }

    public GenericCapabilityVO(String id, String namespace) {
        super(id, namespace);
    }

    /**
     * List of capability attributes.
     */
    private List<AttributeVO> attributes = new ArrayList<>();
    /**
     * List of subcapabilities.
     */
    private List<GenericCapabilityVO> capabilities = new ArrayList<>();
    /**
     * List of capability's requirements.
     */
    private List<GenericRequirementVO> requirements = new ArrayList<>();

    public List<AttributeVO> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributeVO> attributes) {
        this.attributes = attributes;
    }

    public List<GenericCapabilityVO> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(List<GenericCapabilityVO> capabilities) {
        this.capabilities = capabilities;
    }

    public List<GenericRequirementVO> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<GenericRequirementVO> requirements) {
        this.requirements = requirements;
    }
}

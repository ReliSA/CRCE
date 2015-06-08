package cz.zcu.kiv.crce.vo.model.metadata;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * VO containing full information about a resource.
 *
 * List of all its capabilities, requirements, properties, etc.
 *
 * Date: 5.5.15
 *
 * @author Jakub Danek
 */
@XmlRootElement(name = "resource")
public class DetailedResourceVO extends BasicResourceVO {

    private List<GenericCapabilityVO> capabilities = new ArrayList<>();
    private List<PropertyVO> properties = new ArrayList<>();
    private List<GenericRequirementVO> requirements = new ArrayList<>();

    public DetailedResourceVO() {
    }

    public DetailedResourceVO(String id, IdentityCapabilityVO identity) {
        super(id, identity);
    }

    public DetailedResourceVO(IdentityCapabilityVO identity) {
        super(identity);
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
    public List<GenericRequirementVO> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<GenericRequirementVO> requirements) {
        this.requirements = requirements;
    }

    @Nonnull
    @XmlElementRef
    public List<PropertyVO> getProperties() {
        return properties;
    }

    public void setProperties(List<PropertyVO> properties) {
        this.properties = properties;
    }
}

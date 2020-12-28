package cz.zcu.kiv.crce.vo.model.metadata;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import cz.zcu.kiv.crce.vo.model.ValueObject;

/**
 *
 * Value object for {@link cz.zcu.kiv.crce.metadata.Resource}
 * Contains only resource's identity view.
 *
 * For use in simple listings.
 *
 * Date: 5.5.15
 *
 * @author Jakub Danek
 */
@XmlRootElement(name = "resource")
public class BasicResourceVO extends ValueObject {

    /**
     * Identity of the resource.
     */
    private IdentityCapabilityVO identity;

    public BasicResourceVO() {
    }

    public BasicResourceVO(String id, IdentityCapabilityVO identity) {
        super(id);
        this.identity = identity;
    }

    public BasicResourceVO(IdentityCapabilityVO identity) {
        this(null, identity);
    }

    @XmlElementRef(required = true)
    public IdentityCapabilityVO getIdentity() {
        return identity;
    }

    public void setIdentity(IdentityCapabilityVO identity) {
        this.identity = identity;
    }
}


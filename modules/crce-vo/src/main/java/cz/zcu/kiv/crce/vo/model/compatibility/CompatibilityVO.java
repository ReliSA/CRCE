package cz.zcu.kiv.crce.vo.model.compatibility;

import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import cz.zcu.kiv.crce.compatibility.Contract;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.compatibility.namespace.NsCrceCompatibility;
import cz.zcu.kiv.crce.vo.model.ValueObject;

/**
 * Date: 4.9.15
 *
 * @author Jakub Danek
 */
@XmlRootElement(name = "compatibility")
public class CompatibilityVO extends ValueObject {

    /**
     *  Name of the resource (crce.identity namespace) which has been compared to the Base resource
     */
    private String otherExternalId;
    /**
     * Version of the resource which was compared to the Base resource.
     * <p/>
     * Implemented according to the OSGi collection scheme:
     * major.minor.micor-qualifier
     */
    private String otherVersion;
    /**
     *  Name of the resource (crce.identity namespace) which was the reference Resource
     */
    private String baseExternalId;
    /**
     * Version of the resource which the resource has been compared to.
     * <p/>
     * Implemented according to the OSGi collection scheme:
     * major.minor.micor-qualifier
     */
    private String baseVersion;
    /**
     *  Difference value for the two resources aggregated from the DiffDetails.
     */
    private Difference diffValue;
    /**
     * contract the compatibility instance is related to
     */
    private Contract contract;
    /**
     * Complete diff of the two resources.
     */
    private List<DiffVO> diffs;

    public CompatibilityVO() {
        this.setNamespace(NsCrceCompatibility.NAMESPACE__CRCE_COMPATIBILITY);
    }

    public CompatibilityVO(String id) {
        super(id, NsCrceCompatibility.NAMESPACE__CRCE_COMPATIBILITY);
    }

    @XmlAttribute(name = "externalId")
    public String getOtherExternalId() {
        return otherExternalId;
    }

    public void setOtherExternalId(String otherExternalId) {
        this.otherExternalId = otherExternalId;
    }

    @XmlAttribute(name = "version")
    public String getOtherVersion() {
        return otherVersion;
    }

    public void setOtherVersion(String otherVersion) {
        this.otherVersion = otherVersion;
    }

    @XmlAttribute(name = "baseExternalId")
    public String getBaseExternalId() {
        return baseExternalId;
    }

    public void setBaseExternalId(String baseExternalId) {
        this.baseExternalId = baseExternalId;
    }

    @XmlAttribute(name = "baseVersion")
    public String getBaseVersion() {
        return baseVersion;
    }

    public void setBaseVersion(String baseVersion) {
        this.baseVersion = baseVersion;
    }

    @XmlAttribute(name = "diff")
    public Difference getDiffValue() {
        return diffValue;
    }

    public void setDiffValue(Difference diffValue) {
        this.diffValue = diffValue;
    }

    @XmlAttribute(name= "contract")
    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    @Nonnull
    @XmlElementRef
    public List<DiffVO> getDiffs() {
        return diffs;
    }

    public void setDiffs(List<DiffVO> diffs) {
        this.diffs = diffs;
    }
}

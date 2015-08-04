package cz.zcu.kiv.crce.vo.model.metadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.StringUtils;

import cz.zcu.kiv.crce.metadata.namespace.NsCrceIdentity;
import cz.zcu.kiv.crce.vo.model.ValueObject;

/**
 *
 * Easy-to-use view of CRCE Identity Capability instances.
 *
 * Date: 5.5.15
 *
 * @author Jakub Danek
 */
@ParametersAreNonnullByDefault
@XmlRootElement(name = "capability")
public class IdentityCapabilityVO extends ValueObject {

    /**
     * CRCE Readable name. E.g. osgi symbolic name.
     */
    private AttributeVO name;
    /**
     * Resource version string representation
     */
    private AttributeVO version;
    /**
     * Original version of the resource (if CRCE calculates
     * the version itself).
     */
    private AttributeVO originalVersion;
    /**
     * Size of the resource binary.
     */
    private AttributeVO size;
    /**
     * List of resource Type tags
     */
    private AttributeVO types;
    /**
     * List of resource Category tags.
     */
    private AttributeVO categories;

    public IdentityCapabilityVO() {
        this("");
    }

    public IdentityCapabilityVO(String id) {
        super(id, NsCrceIdentity.NAMESPACE__CRCE_IDENTITY);
    }

    public IdentityCapabilityVO(String id, String name, String version) {
        this(id);
        this.name = new AttributeVO(NsCrceIdentity.ATTRIBUTE__NAME.getName(), name);
        this.version = new AttributeVO(NsCrceIdentity.ATTRIBUTE__VERSION.getName(), version);
    }

    /*
    ################MAPPING INTERFACE #########################
     */

    @XmlElementRef
    AttributeVO getNameAt() {
        return name;
    }

    void setNameAt(AttributeVO nameAt) {
        this.name = nameAt;
    }

    @XmlElementRef
    AttributeVO getVersionAt() {
        return version;
    }

    void setVersionAt(AttributeVO versionAt) {
        this.version = versionAt;
    }

    @XmlElementRef
    AttributeVO getOriginalVersionAt() {
        return originalVersion;
    }

    void setOriginalVersionAt(AttributeVO originalVersionAt) {
        this.originalVersion = originalVersionAt;
    }

    @XmlElementRef
    AttributeVO getSizeAt() {
        return size;
    }

    void setSizeAt(AttributeVO sizeAt) {
        this.size = sizeAt;
    }

    @XmlElementRef
    AttributeVO getTypesAt() {
        return types;
    }

    void setTypesAt(AttributeVO typesAt) {
        this.types = typesAt;
    }

    @XmlElementRef
    AttributeVO getCategoriesAt() {
        return categories;
    }

    void setCategoriesAt(AttributeVO categoriesAt) {
        this.categories = categoriesAt;
    }

    /*
     ################### PUBLIC INTERFACE #########################
     */
    @Nonnull
    public String getName() {
        return name.getValue();
    }

    protected void setName(String name) {
        this.name.setName(name);
    }

    @Nonnull
    public String getVersion() {
        return version.getValue();
    }

    protected void setVersion(String version) {
        this.version.setValue(version);
    }

    @Nullable
    public String getOriginalVersion() {
        if(originalVersion == null) {
            return null;
        }
        return originalVersion.getValue();
    }

    public void setOriginalVersion(String originalVersion) {
        if(this.originalVersion == null) {
            this.originalVersion = new AttributeVO(NsCrceIdentity.ATTRIBUTE__VERSION.getName(), originalVersion);
        } else {
            this.originalVersion.setValue(originalVersion);
        }
    }

    @Nullable
    @XmlTransient
    public Long getSize() {
        if(size == null) {
            return null;
        }
        return Long.parseLong(size.getValue());
    }

    public void setSize(Long size) {
        if(this.size == null) {
            this.size = new AttributeVO(NsCrceIdentity.ATTRIBUTE__SIZE.getName(), size.toString());
        } else {
            this.size.setValue(size.toString());
        }
    }

    @Nonnull
    public List getTypes() {
        if(this.types == null) {
            return new ArrayList<>();
        }
        return Arrays.asList(types.getValue().split(","));
    }

    protected void setTypes(@Nonnull List types) {
        String value = StringUtils.join(types, ",");
        if(this.types == null) {
            this.types = new AttributeVO(NsCrceIdentity.ATTRIBUTE__TYPES.getName(), value);
        } else {
            this.types.setValue(value);
        }
    }

    @Nonnull
    public List<String> getCategories() {
        if(categories == null) {
            return new ArrayList<>();
        }
        return Arrays.asList(categories.getValue().split(","));
    }

    public void addCategory(String cat) {
        if(categories == null) {
            this.categories = new AttributeVO(NsCrceIdentity.ATTRIBUTE__CATEGORIES.getName(), cat);
        } else {
            String oldVal = this.categories.getValue();
            oldVal = oldVal.concat("," + cat);
            this.categories.setValue(oldVal);
        }
    }


    protected void setCategories(@Nonnull List<String> categories) {
        String value = StringUtils.join(categories, ",");
        if(this.categories == null) {
            this.categories = new AttributeVO(NsCrceIdentity.ATTRIBUTE__CATEGORIES.getName(), value);
        } else {
            this.categories.setValue(value);
        }
    }
}

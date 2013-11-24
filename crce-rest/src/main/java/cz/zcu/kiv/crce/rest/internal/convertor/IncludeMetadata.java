package cz.zcu.kiv.crce.rest.internal.convertor;

/**
 * Filter criteria for GET Metadata operation.
 *
 * Filter criteria determines, which parts of XML with metedata should be included.
 *
 *
 * @author Jan Reznicek
 *
 */
public class IncludeMetadata {

    private boolean includeCore;

    private boolean includeCaps;
    private String includeCapByName;

    private boolean includeReqs;
    private String includeReqByName;

    private boolean includeProps;
    private String includePropByName;

    /**
     * At default, including of each part of metadata is set to false.
     */
    public IncludeMetadata() {
        includeCore = false;

        includeCaps = false;
        includeCapByName = null;

        includeReqs = false;
        includeReqByName = null;

        includeProps = false;
        includePropByName = null;
    }

    public boolean isIncludeCore() {
        return includeCore;
    }

    public void setIncludeCore(boolean includeCore) {
        this.includeCore = includeCore;
    }

    public boolean isIncludeCaps() {
        return includeCaps;
    }

    public void setIncludeCaps(boolean includeCaps) {
        this.includeCaps = includeCaps;
    }

    public String getIncludeCapByName() {
        return includeCapByName;
    }

    public void setIncludeCapByName(String includeCapByName) {
        this.includeCapByName = includeCapByName;
    }

    public boolean isIncludeReqs() {
        return includeReqs;
    }

    public void setIncludeReqs(boolean includeReqs) {
        this.includeReqs = includeReqs;
    }

    public String getIncludeReqByName() {
        return includeReqByName;
    }

    public void setIncludeReqByName(String includeReqByName) {
        this.includeReqByName = includeReqByName;
    }

    public boolean isIncludeProps() {
        return includeProps;
    }

    public void setIncludeProps(boolean includeProps) {
        this.includeProps = includeProps;
    }

    public String getIncludePropByName() {
        return includePropByName;
    }

    public void setIncludePropByName(String includePropByName) {
        this.includePropByName = includePropByName;
    }

    /**
     * Include all parts of metadata.
     */
    public void includeAll() {
        includeCore = true;
        includeCaps = true;
        includeReqs = true;
        includeProps = true;

    }

    /**
     * Determine, if capability metadata should be included.
     *
     * @param capName name of capability
     * @return boolean, if capability metadata should be included.
     */
    public boolean shloudIncludeCap(String capName) {
        if (includeCore && (capName.endsWith(".content") || capName.endsWith(".identity"))) {
            //core capability
            return true;
        }

        if (includeCaps && includeCapByName == null) {
            //all capabilities are included
            return true;
        }

        if (includeCaps && includeCapByName.equals(capName)) {
            //capability of this name should be included
            return true;
        }

        return false;
    }

    /**
     * Determine, if requirement metadata should be included.
     *
     * @param reqName name of requirement
     * @return boolean, if requirement metadata should be included.
     */
    public boolean shloudIncludeReq(String reqName) {

        if (includeReqs && includeReqByName == null) {
            //all requirements are included
            return true;
        }

        if (includeReqs && includeReqByName.equals(reqName)) {
            //requirement of this name should be included
            return true;
        }

        return false;
    }

    /**
     * Determine, if property metadata should be included.
     *
     * @param propName name of property
     * @return boolean, if property metadata should be included.
     */
    public boolean shloudIncludeProp(String propName) {

        if (includeProps && includePropByName == null) {
            //all property are included
            return true;
        }

        if (includeProps && includePropByName.equals(propName)) {
            //property of this name should be included
            return true;
        }

        return false;
    }

}

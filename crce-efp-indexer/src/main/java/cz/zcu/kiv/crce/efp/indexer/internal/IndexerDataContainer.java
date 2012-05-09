package cz.zcu.kiv.crce.efp.indexer.internal;

import java.util.List;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.efps.assignment.api.ComponentEfpAccessor;
import cz.zcu.kiv.efps.assignment.types.Feature;
import cz.zcu.kiv.efps.types.lr.LR;

/**
 * Contains instances and variables with data for indexing purpose.
 */
public class IndexerDataContainer {

    /** This interface accesses EFPs attached to a component.
     *  It allows to read all feature, EFPs and values attached to the EFPs on the component. */
    private ComponentEfpAccessor accessor;

    /** In OSGi context this list contain exported or imported packages. */
    private List<Feature> featureList;

    /** Local Registry array. */
    private LR []arrayLR;

    /** Resource can be OSGi bundle or other artifact uploaded into CRCE buffer. */
    private Resource resource;

    /** Prefix string for identification this module messages in MetadataIndexingResultService. */
    public static final String EFP_INDEXER_MODULE = "crce-efp-indexer: ";

    /** Length of Efp prefix in Efp-data-types like EfpNumberInterval.
     *  Constant is used for removing this "Efp" prefix. */
    public static final int EFP_PREFIX_LENGTH = 3;

    /** This constant is used in logMessage() method when information is only for MetadataIndexingResultService. */
    public static final int NO_LOGSERVICE_MESSAGE = 0;

    //------------------------------------------
    // Getters and Setters

    /**
     * @return the accessor
     */
    public final ComponentEfpAccessor getAccessor() {
        return accessor;
    }

    /**
     * @param accessor the accessor to set
     */
    public final void setAccessor(final ComponentEfpAccessor accessor) {
        this.accessor = accessor;
    }

    /**
     * @return the featureList
     */
    public final List<Feature> getFeatureList() {
        return featureList;
    }

    /**
     * @param featureList the featureList to set
     */
    public final void setFeatureList(final List<Feature> featureList) {
        this.featureList = featureList;
    }

    /**
     * @return the arrayLR
     */
    public final LR[] getArrayLR() {
        return arrayLR;
    }

    /**
     * @param arrayLR the arrayLR to set
     */
    public final void setArrayLR(final LR[] arrayLR) {
        this.arrayLR = arrayLR;
    }

    /**
     * @return the resource
     */
    public final Resource getResource() {
        return resource;
    }

    /**
     * @param resource the resource to set
     */
    public final void setResource(final Resource resource) {
        this.resource = resource;
    }

}

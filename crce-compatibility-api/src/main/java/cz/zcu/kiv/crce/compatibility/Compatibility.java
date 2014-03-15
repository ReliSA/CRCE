package cz.zcu.kiv.crce.compatibility;

import java.util.List;

import cz.zcu.kiv.typescmp.Difference;

import cz.zcu.kiv.crce.metadata.type.Version;

/**
 * Interface representing Compability metadata for a pair of bundles.
 * <p/>
 * Date: 16.11.13
 *
 * @author Jakub Danek
 */
public interface Compatibility {

    /**
     * Id implementation depends on underlying persistence layer.
     *
     * @return unique ID
     */
    String getId();

    /**
     * Name of the resource (crce.identity namespace) which has been compared to the Base resource
     *
     * @return unique name
     */
    String getResourceName();

    /**
     * Version of the resource which was compared to the Base resource.
     * <p/>
     * Implemented according to the OSGi versioning scheme:
     * major.minor.micor-qualifier
     *
     * @return version object
     */
    Version getResourceVersion();

    /**
     * Name of the resource (crce.identity namespace) which was the Resource has been compared to
     *
     * @return unique name
     */
    String getBaseResourceName();

    /**
     * Version of the resource which the resource has been compared to.
     * <p/>
     * Implemented according to the OSGi versioning scheme:
     * major.minor.micor-qualifier
     *
     * @return version object
     */
    Version getBaseResourceVersion();

    /**
     * Difference value for the two resources aggregated from the DiffDetails.
     *
     * @return {@link Difference} value
     */
    Difference getDiffValue();

    /**
     * @return Complete diff of the two resources.
     */
    List<Diff> getDiffDetails();

}

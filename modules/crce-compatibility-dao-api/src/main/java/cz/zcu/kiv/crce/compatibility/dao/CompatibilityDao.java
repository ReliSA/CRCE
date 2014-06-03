package cz.zcu.kiv.crce.compatibility.dao;

import java.util.List;

import cz.zcu.kiv.typescmp.Difference;

import cz.zcu.kiv.crce.compatibility.Compatibility;
import cz.zcu.kiv.crce.metadata.type.Version;

/**
 * Interface for Compatibility DAO object.
 * <p/>
 * Date: 16.11.13
 *
 * @author Jakub Danek
 */
public interface CompatibilityDao {

    /**
     * @param id unique ID, implementation depends on underlying persistence design
     * @return compatibility with the given ID or null if not found
     */
    Compatibility readCompability(String id);

    /**
     * Saves new Compatibility or updates existing.
     *
     * @param compatibility compatibility to create or update (difference recognition depends on underlying implemetnation)
     * @return created/persisted object (ensured to have ID set)
     */
    Compatibility saveCompatibility(Compatibility compatibility);

    /**
     * Deletes the compability from persistence.
     *
     * @param compatibility compatibility to delete
     */
    void deleteCompatibility(Compatibility compatibility);

    /**
     * Deletes all compatibility data related to a resource with the given name an version.
     * That is it doesnt matter whether the resource has been the new version or the base.
     *
     * @param resourceName    symbolic name
     * @param resourceVersion version
     */
    void deleteAllRelatedCompatibilities(String resourceName, Version resourceVersion);

    /**
     * List of all compatibilities owned by a resource with the given name and version
     * i.e. all Compatibility data where this resource has been the "new" version
     *
     * @param resourceName    name of the resource
     * @param resourceVersion version of the resource
     * @return
     */
    List<Compatibility> listOwnedCompatibilities(String resourceName, Version resourceVersion);

    /*
            List of differences
     */

    /**
     * Returns Compatibility data for provided baseName and with resource version higher than
     * the base version provided.
     * <p/>
     * The diffValue of Compatibility must be one of those in differences list.
     *
     * @param baseName    requested base name
     * @param baseVersion requested base version
     * @param differences list of permitted difference values
     * @return list of compatibilities or empty list if none found
     */
    List<Compatibility> findHigher(String baseName, Version baseVersion, List<Difference> differences);

    /**
     * Returns Compatibility data for provided resourceName and with base version lower than
     * the resource version provided.
     * <p/>
     * The diffValue of Compatibility must be one of those in differences list.
     *
     * @param resourceName    requested base name
     * @param resourceVersion requested base version
     * @param differences     list of permitted difference values
     * @return list of compatibilities or empty list if none found
     */
    List<Compatibility> findLower(String resourceName, Version resourceVersion, List<Difference> differences);
}

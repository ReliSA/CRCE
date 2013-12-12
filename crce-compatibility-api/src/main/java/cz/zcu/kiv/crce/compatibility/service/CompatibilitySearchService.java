package cz.zcu.kiv.crce.compatibility.service;

import java.util.List;

import cz.zcu.kiv.crce.compatibility.Compatibility;
import cz.zcu.kiv.crce.metadata.Resource;

/**
 * Service for searching compatibilities or resources by compatibility criteria.
 *
 * Doesnt contain services for modification of compatibility data.
 *
 * Date: 20.11.13
 *
 * @author Jakub Danek
 */
public interface CompatibilitySearchService {

    /**
     * List all compatibility data of the given resource with upper version.
     * @param resource resource present in crce
     * @return list
     */
    List<Compatibility> listUpperCompatibilities(Resource resource);

    /**
     * List all compatibility data of the given resource with lower versions.
     * @param resource resource present in crce
     * @return list
     */
    List<Compatibility> listLowerCompatibilities(Resource resource);

    /**
     * Find the nearest version of the same resource name which is available for upgrade (has higher
     * version and is strictly compatible).
     *
     * @param resource resource to upgrade
     * @return strictly compatible resource or null if none found
     */
    Resource findNearestUpgrade(Resource resource);

    /**
     * Find the highest version of the same resource name which is available for upgrade (has higher
     * version and is strictly compatible).
     *
     * @param resource resource to upgrade
     * @return strictly compatible resource or null if none found
     */
    Resource findHighestUpgrade(Resource resource);

    /**
     * Find the highest version of the same resource name which is available for downgrade (has lower
     * version and is strictly compatible).
     *
     * @param resource resource to upgrade
     * @return strictly compatible resource or null if none found
     */
    Resource findNearestDowngrade(Resource resource);

    /**
     * Find the lowest version of the same resource name which is available for downgrage (has lower
     * version and is strictly compatible).
     *
     * @param resource resource to upgrade
     * @return strictly compatible resource or null if none found
     */
    Resource findLowestDowngrade(Resource resource);
}

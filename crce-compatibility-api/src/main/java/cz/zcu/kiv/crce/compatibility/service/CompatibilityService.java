package cz.zcu.kiv.crce.compatibility.service;

import java.util.List;

import cz.zcu.kiv.crce.compatibility.Compatibility;
import cz.zcu.kiv.crce.metadata.Resource;
/**
 * Service for Compatibility data management.
 *
 * Allows user to create and modify Compatibility data. For convenience provides all available search capabilities
 * as well.
 *
 * Date: 19.11.13
 *
 * @author Jakub Danek
 */
public interface CompatibilityService extends CompatibilitySearchService {
    /**
     * Calculates Compatibility data between the {@code resource} and the list of {@code additionalBaseResources}
     * @param resource new resource
     * @param additionalBaseResources list of resources to be used as base
     * @return calculated compatibilities
     */
    List<Compatibility> calculateAdditionalCompatibilities(Resource resource, List<Resource> additionalBaseResources);

    /**
     * Calculates Compatibility data between {@code resource} and all its previous versions.
     * @param resource new resource
     * @return calculated compatibilities
     */
    List<Compatibility> calculateCompatibilities(Resource resource);

    /**
     * Removes all compatibility information about the resource
     * i.e. all Compatibilities where the resource has been used as either
     * upper or lower value.
     * @param resource the resource to be removed
     */
    void removeCompatibilities(Resource resource);
}

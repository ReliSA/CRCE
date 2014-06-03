package cz.zcu.kiv.crce.compatibility.internal.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.jacc.loader.JClassLoaderCreator;
import cz.zcu.kiv.obcc.bundleCmp.DefaultJOSGiComparatorFactory;
import cz.zcu.kiv.obcc.bundleCmp.DefaultJOSGiComparatorState;
import cz.zcu.kiv.obcc.bundleCmp.JOSGiComparator;
import cz.zcu.kiv.obcc.bundleCmp.JOSGiComparatorState;
import cz.zcu.kiv.obcc.bundleloader.BundleMetadataReaderCreator;
import cz.zcu.kiv.obcc.bundletypes.JOSGiBundle;
import cz.zcu.kiv.obcc.loader.JOSGiBundleLoader;
import cz.zcu.kiv.obcc.loader.impl.JOSGiBundleLoaderImpl;
import cz.zcu.kiv.typescmp.CmpResult;
import cz.zcu.kiv.typescmp.Difference;

import cz.zcu.kiv.crce.compatibility.Compatibility;
import cz.zcu.kiv.crce.compatibility.CompatibilityFactory;
import cz.zcu.kiv.crce.compatibility.CompatibilityVersionComparator;
import cz.zcu.kiv.crce.compatibility.Contract;
import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.dao.CompatibilityDao;
import cz.zcu.kiv.crce.compatibility.service.CompatibilityService;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.Operator;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.osgi.namespace.NsOsgiIdentity;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.metadata.type.Version;
import cz.zcu.kiv.crce.repository.Store;

/**
 * Date: 27.11.13
 *
 * @author Jakub Danek
 */
public class CompatibilityServiceImpl implements CompatibilityService {

    private static final Logger logger = LoggerFactory.getLogger(CompatibilityServiceImpl.class);

    private CompatibilityFactory compatibilityFactory;     //injected by dependency manager
    private Store store;                                   //injected by dependency manager
    private CompatibilityDao compatibilityDao;             //injected by dependency manager
    private MetadataService metadataService;               //injected by dependency manager
    private MetadataFactory metadataFactory;               //injected by dependency manager

    private final JOSGiComparator<JOSGiBundle> comparator = new DefaultJOSGiComparatorFactory().getBundleComparator();
    private final JOSGiBundleLoader loader = new JOSGiBundleLoaderImpl(JClassLoaderCreator.JAR_ASM_LOADER, BundleMetadataReaderCreator.JAR_FILE_READER);

    private static final List<Difference> UPGRADE_DIFFS = Arrays.asList(Difference.INS, Difference.NON);
    private static final List<Difference> DOWNGRADE_DIFFS = Arrays.asList(Difference.DEL, Difference.NON);

    /**
     * Calculates Compatibility data between the {@code resource} and the list of {@code additionalBaseResources}
     *
     * @param resource                new resource
     * @param additionalBaseResources list of resources to be used as base
     * @return calculated compatibilities
     */
    @Override
    public List<Compatibility> calculateAdditionalCompatibilities(Resource resource, List<Resource> additionalBaseResources) {
        return createMultipleCompatibilityData(resource, additionalBaseResources);
    }

    /**
     * Calculates Compatibility data between {@code resource} and all its previous versions.
     *
     * @param resource new resource
     * @return calculated compatibilities
     */
    @Override
    public List<Compatibility> calculateCompatibilities(Resource resource) {

        //get required filter information
        OsgiIdentity identity = loadOsgiIdentity(resource);

        //create filter
        Requirement filter = metadataFactory.createRequirement(NsOsgiIdentity.NAMESPACE__OSGI_IDENTITY);
        filter.addAttribute(NsOsgiIdentity.ATTRIBUTE__SYMBOLIC_NAME, identity.symbolicName);
        filter.addAttribute(NsOsgiIdentity.ATTRIBUTE__VERSION, identity.version, Operator.LESS);

        logger.debug("Searching for resources with symbolic name: {} and version: {}", identity.symbolicName, identity.version);
        List<Resource> previous = store.getResources(filter);
        logger.debug("{} resources found.", previous.size());

        return createMultipleCompatibilityData(resource, previous);
    }

    /**
     * Removes all compatibility information about the resource
     * i.e. all Compatibilities where the resource has been used as either
     * upper or lower value.
     *
     * @param resource the resource to be removed
     */
    @Override
    public void removeCompatibilities(Resource resource) {
        OsgiIdentity identity = loadOsgiIdentity(resource);
        compatibilityDao.deleteAllRelatedCompatibilities(identity.symbolicName, identity.version);
    }

    /**
     * List all compatibility data of the given resource with higher versions.
     *
     * @param resource resource present in crce
     * @return list
     */
    @Override
    public List<Compatibility> listUpperCompatibilities(Resource resource) {
        OsgiIdentity identity = loadOsgiIdentity(resource);
        return compatibilityDao.findHigher(identity.symbolicName, identity.version, Arrays.asList(Difference.values()));
    }

    /**
     * List all compatibility data of the given resource with lower versions.
     *
     * @param resource resource present in crce
     * @return list
     */
    @Override
    public List<Compatibility> listLowerCompatibilities(Resource resource) {
        OsgiIdentity identity = loadOsgiIdentity(resource);
        return compatibilityDao.findLower(identity.symbolicName, identity.version, Arrays.asList(Difference.values()));
    }

    /**
     * Find the nearest version of the same resource name which is available for upgrade (has higher
     * version and is strictly compatible).
     *
     * @param resource resource to upgrade
     * @return strictly compatible resource or null if none found
     */
    @Override
    public Resource findNearestUpgrade(Resource resource) {
        return findSuitableResource(resource, true, 0);
    }

    /**
     * Find the highest version of the same resource name which is available for upgrade (has higher
     * version and is strictly compatible).
     *
     * @param resource resource to upgrade
     * @return strictly compatible resource or null if none found
     */
    @Override
    public Resource findHighestUpgrade(Resource resource) {
        return findSuitableResource(resource, true);
    }

    /**
     * Find the highest version of the same resource name which is available for downgrade (has lower
     * version and is strictly compatible).
     *
     * @param resource resource to upgrade
     * @return strictly compatible resource or null if none found
     */
    @Override
    public Resource findNearestDowngrade(Resource resource) {
        return findSuitableResource(resource, false);
    }

    /**
     * Find the lowest version of the same resource name which is available for downgrage (has lower
     * version and is strictly compatible).
     *
     * @param resource resource to upgrade
     * @return strictly compatible resource or null if none found
     */
    @Override
    public Resource findLowestDowngrade(Resource resource) {
        return findSuitableResource(resource, false, 0);
    }

    private Resource findSuitableResource(Resource resource, boolean upgrade, Integer index) {
        List<Compatibility> candidates;

        OsgiIdentity identity = loadOsgiIdentity(resource);
        if (upgrade) {
            candidates = compatibilityDao.findHigher(identity.symbolicName, identity.version, UPGRADE_DIFFS);
        } else {
            candidates = compatibilityDao.findLower(identity.symbolicName, identity.version, DOWNGRADE_DIFFS);
        }

        if (!candidates.isEmpty()) {
            //pick highest if no special index provided
            int i = index == null ? candidates.size() - 1 : index;

            Collections.sort(candidates, CompatibilityVersionComparator.getUpperComparator());
            Compatibility winner = candidates.get(i);

            String winnerName = upgrade ? winner.getResourceName() : winner.getBaseResourceName();
            Version winnerVersion = upgrade ? winner.getResourceVersion() : winner.getBaseResourceVersion();

            Requirement filter = metadataFactory.createRequirement(NsOsgiIdentity.NAMESPACE__OSGI_IDENTITY);
            filter.addAttribute(NsOsgiIdentity.ATTRIBUTE__SYMBOLIC_NAME, winnerName);
            filter.addAttribute(NsOsgiIdentity.ATTRIBUTE__VERSION, winnerVersion);

            List<Resource> r = store.getResources(filter);
            if (r.size() == 1) {
                return r.get(0);
            }
        }

        return null;
    }

    private Resource findSuitableResource(Resource res, boolean upgrade) {
        return findSuitableResource(res, upgrade, null);
    }

    private List<Compatibility> createMultipleCompatibilityData(Resource upper, List<Resource> lowers) {
        List<Compatibility> created = new ArrayList<>(lowers.size());

        Compatibility comp;
        for (Resource lower : lowers) {
            comp = createSingleCompatibilityData(upper, lower);
            created.add(comp);
        }

        return created;
    }

    private Compatibility createSingleCompatibilityData(Resource upper, Resource lower) {
        OsgiIdentity lowerIdentity = loadOsgiIdentity(lower);
        OsgiIdentity upperIdentity = loadOsgiIdentity(upper);

        if (logger.isDebugEnabled()) {
            logger.debug("Calculating compatibility between {}-{} and {}-{}",
                    lowerIdentity.symbolicName, lowerIdentity.version, upperIdentity.symbolicName, upperIdentity.version);
        }

        JOSGiBundle upperBundle = getBundleRepresentation(upper);
        logger.debug("Upper bundle representation acquired.");
        JOSGiBundle lowerBundle = getBundleRepresentation(lower);
        logger.debug("Lower bundle representation acquired.");

        JOSGiComparatorState state = new DefaultJOSGiComparatorState();
        CmpResult<JOSGiBundle> res = comparator.compare(lowerBundle, upperBundle, state);
        logger.debug("Bundles compared successfully.");

        Version upperVersion = upperIdentity.version;
        Version lowerVersion = lowerIdentity.version;

        Difference diffValue = res.getDiff();

        CmpResultParser parser = new CmpResultParser(compatibilityFactory);
        List<Diff> diffDetails = parser.extractDiffDetails(res);

        Compatibility comp = compatibilityFactory.createCompatibility(null, upperIdentity.symbolicName, upperVersion, lowerIdentity.symbolicName, lowerVersion, diffValue, diffDetails, Contract.SYNTAX);
        comp = compatibilityDao.saveCompatibility(comp);
        logger.debug("Compatibility saved successfully with id: {}", comp.getId());
        return comp;
    }

    private JOSGiBundle getBundleRepresentation(Resource resource) {
        URI uri = metadataService.getUri(resource);
        return loader.loadBundleRepresentation(uri.toString());
    }

    //////////////////////////////////////////////////////////////////
    private static final class OsgiIdentity {
        public String symbolicName;
        public Version version;
    }

    private OsgiIdentity loadOsgiIdentity(Resource resource) {
        OsgiIdentity identity = new OsgiIdentity();

        Capability osgiIdentity = metadataService.getSingletonCapability(resource, NsOsgiIdentity.NAMESPACE__OSGI_IDENTITY);
        identity.symbolicName = osgiIdentity.getAttributeStringValue(NsOsgiIdentity.ATTRIBUTE__SYMBOLIC_NAME);
        identity.version = osgiIdentity.getAttributeValue(NsOsgiIdentity.ATTRIBUTE__VERSION);

        return identity;
    }
}

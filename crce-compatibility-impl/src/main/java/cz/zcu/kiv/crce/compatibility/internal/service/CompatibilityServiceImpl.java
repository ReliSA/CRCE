package cz.zcu.kiv.crce.compatibility.internal.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.Version;
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
import cz.zcu.kiv.crce.compatibility.dao.CompatibilityDao;
import cz.zcu.kiv.crce.compatibility.service.CompatibilityService;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.repository.Store;

/**
 * Date: 27.11.13
 *
 * @author Jakub Danek
 */
public class CompatibilityServiceImpl implements CompatibilityService {

    private static final Logger logger = LoggerFactory.getLogger(CompatibilityServiceImpl.class);

    private final JOSGiComparator<JOSGiBundle> comparator = new DefaultJOSGiComparatorFactory().getBundleComparator();
    private final JOSGiBundleLoader loader = new JOSGiBundleLoaderImpl(JClassLoaderCreator.JAR_ASM_LOADER, BundleMetadataReaderCreator.JAR_FILE_READER);

    private static final List<Difference> UPGRADE_DIFFS = Arrays.asList(new Difference[] {Difference.INS,  Difference.NON});
    private static final List<Difference> DOWNGRADE_DIFFS = Arrays.asList(new Difference[] {Difference.DEL, Difference.NON});

    private CompatibilityFactory m_compatibilityFactory;     //injected by dependency manager
    private Store m_store;                                   //injected by dependency manager
    private CompatibilityDao m_compatibilityDao;             //injected by dependency manager

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
        List<Compatibility> compatibilities;
        try {
            String filter = "(&(symbolicname=" + resource.getSymbolicName() + ")(!(version>=" + resource.getVersion().toString() + ")))";
            logger.debug("Searching for resources with filter: {}", filter);
            Resource previous[] = m_store.getRepository().getResources(filter);
            logger.debug("{} resources found.", previous.length);

            compatibilities = createMultipleCompatibilityData(resource, Arrays.asList(previous));

            return compatibilities;
        } catch (InvalidSyntaxException e) {
            logger.error("Invalid filter syntax! No compatibility data calculated.", e);
            return new ArrayList<>();
        }
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
        m_compatibilityDao.deleteAllRelatedCompabilities(resource.getSymbolicName(), resource.getVersion());
    }

    /**
     * List all available compatibility data for the given resource.
     *
     * @param resource resource present in crce
     * @return list
     */
    @Override
    public List<Compatibility> listCompatibilities(Resource resource) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
        if(upgrade) {
            candidates = m_compatibilityDao.findHigher(resource.getSymbolicName(), resource.getVersion(), UPGRADE_DIFFS);
        } else {
            candidates = m_compatibilityDao.findLower(resource.getSymbolicName(), resource.getVersion(), DOWNGRADE_DIFFS);
        }

        if(!candidates.isEmpty()) {
            //pick highest if no special index provided
            int i = index == null ? candidates.size() -1 : index;

            Collections.sort(candidates, CompatibilityVersionComparator.getUpperComparator());
            Compatibility winner = candidates.get(i);

            String winnerName = upgrade ? winner.getResourceName() : winner.getBaseResourceName();
            String winnerVersion = upgrade ? winner.getResourceVersion().toString() : winner.getBaseResourceVersion().toString();

            String filter = "(&(symbolicname=" + winnerName + ")(version=" + winnerVersion + "))";
            try {
                Resource[] r = m_store.getRepository().getResources(filter);
                if(r.length == 1) {
                    return r[0];
                }
            } catch (InvalidSyntaxException e) {
                //TODo LOG
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
        for(Resource lower : lowers) {
            comp = createSingleCompatibilityData(upper, lower);
            created.add(comp);
        }

        return created;
    }

    private Compatibility createSingleCompatibilityData(Resource upper, Resource lower) {
        if(logger.isDebugEnabled()) {
            logger.debug("Calculating compatibility between {}-{} and {}-{}",
                lower.getSymbolicName(), lower.getVersion(), upper.getSymbolicName(), upper.getVersion());
        }

        JOSGiBundle upperBundle = getBundleRepresentation(upper);
        logger.debug("Upper bundle representation acquired.");
        JOSGiBundle lowerBundle = getBundleRepresentation(lower);
        logger.debug("Lower bundle representation acquired.");

        JOSGiComparatorState state = new DefaultJOSGiComparatorState();
        CmpResult<JOSGiBundle> res = comparator.compare(lowerBundle, upperBundle, state);
        logger.debug("Bundles compared successfully.");

        String upperName = upper.getSymbolicName();
        Version upperVersion = upper.getVersion();

        String lowerName = lower.getSymbolicName();
        Version lowerVersion = lower.getVersion();

        Difference diffValue = res.getDiff();

        Compatibility comp =  m_compatibilityFactory.createCompatibility(null, upperName, upperVersion, lowerName, lowerVersion, diffValue, null);
        comp = m_compatibilityDao.saveCompatibility(comp);
        logger.debug("Compatibility saved successfully with id: {}", comp.getId());
        return comp;
    }

    private JOSGiBundle getBundleRepresentation(Resource resource) {
        return loader.loadBundleRepresentation(resource.getUri().toString());
    }
}

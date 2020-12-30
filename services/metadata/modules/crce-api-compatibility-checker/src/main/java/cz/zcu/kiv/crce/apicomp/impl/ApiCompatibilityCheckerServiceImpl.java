package cz.zcu.kiv.crce.apicomp.impl;

import cz.zcu.kiv.crce.apicomp.ApiCompatibilityChecker;
import cz.zcu.kiv.crce.apicomp.ApiCompatibilityCheckerService;
import cz.zcu.kiv.crce.apicomp.impl.restimpl.RestApiCompatibilityChecker;
import cz.zcu.kiv.crce.apicomp.impl.webservice.jsonwsp.JsonWspCompatibilityChecker;
import cz.zcu.kiv.crce.apicomp.impl.webservice.wadl.WadlCompatibilityChecker;
import cz.zcu.kiv.crce.apicomp.impl.webservice.wsdl.WsdlCompatibilityChecker;
import cz.zcu.kiv.crce.apicomp.result.CompatibilityCheckResult;
import cz.zcu.kiv.crce.compatibility.Compatibility;
import cz.zcu.kiv.crce.compatibility.dao.CompatibilityDao;
import cz.zcu.kiv.crce.metadata.Resource;
import org.apache.felix.dm.annotation.api.Component;
import org.apache.felix.dm.annotation.api.ServiceDependency;
import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

/**
 * A service responsible for fetching resources' metadata and passing them to
 * the correct compatibility checker.
 */
@Component(provides = {ApiCompatibilityCheckerService.class, ManagedService.class}, properties = {
        @org.apache.felix.dm.annotation.api.Property(name = Constants.SERVICE_PID, value = ApiCompatibilityCheckerServiceImpl.PID)
})
public class ApiCompatibilityCheckerServiceImpl implements ApiCompatibilityCheckerService, ManagedService {

    private static final Logger logger = LoggerFactory.getLogger(ApiCompatibilityCheckerServiceImpl.class);

    public static final String CFG_PROPERTY__REST_IGNORE_PATH_VERSION = "rest.path.version.ignore";

    public static final String PID = "cz.zcu.kiv.crce.apicomp";

    private List<ApiCompatibilityChecker> availableCheckers;

    @ServiceDependency(required = false)
    private volatile CompatibilityDao compatibilityDao;

    public ApiCompatibilityCheckerServiceImpl() {
        availableCheckers = new ArrayList<>();
        availableCheckers.add(new RestApiCompatibilityChecker());
        availableCheckers.add(new WadlCompatibilityChecker());
        availableCheckers.add(new WsdlCompatibilityChecker());
        availableCheckers.add(new JsonWspCompatibilityChecker());
        logger.debug("New instance of {} created.", getClass().getSimpleName());
    }

    @Override
    public CompatibilityCheckResult compareApis(Resource api1, Resource api2) {

        ApiCompatibilityChecker c1 = pickChecker(api1),
                c2 = pickChecker(api2);

        if (c1 == null || !c1.equals(c2)) {
            logger.warn("Could not pick right checker or APIs are not comparable.");
            return null;
        }

        return c1.compareApis(api1, api2);
    }

    @Override
    public Compatibility findExistingCompatibility(Resource api1, Resource api2) {
        if (compatibilityDao == null) {
            logger.warn("Compatibility dao not available, can't find existing compatibility object.");
            return null;
        }
        return null;
/*         List<Compatibility> compatibilities = compatibilityDao.findCompatibility(api1, api2);
        return compatibilities.isEmpty() ? null : compatibilities.get(0); */
    }

    @Override
    public Compatibility saveCompatibility(Compatibility compatibilityCheckResult) {
        if (compatibilityDao == null) {
            logger.warn("Compatibility dao not available, can't save compatibility object.");
            return compatibilityCheckResult;
        }

        return compatibilityDao.saveCompatibility(compatibilityCheckResult);
    }

    @Override
    public ApiCompatibilityChecker pickChecker(Resource resource) {
        return availableCheckers.stream()
                .filter(checker -> checker.isApiSupported(resource))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void updated(Dictionary<String, ?> properties) throws ConfigurationException {
        logger.debug("Updated: {}.", PID);

        if (properties == null || properties.isEmpty()) {
            logger.warn("Configuration is empty.");
            return;
        }

        Object val = properties.get(CFG_PROPERTY__REST_IGNORE_PATH_VERSION);
        if (val != null) {
            boolean ignoreRestVersion = Boolean.parseBoolean((String)val);
            logger.debug("Setting {}={}.", CFG_PROPERTY__REST_IGNORE_PATH_VERSION, ignoreRestVersion);
            availableCheckers.forEach(checker -> {
                if (checker instanceof RestApiCompatibilityChecker) {
                    ((RestApiCompatibilityChecker)checker).setIgnoreVersionInPath(ignoreRestVersion);
                }
            });
        }
    }

    @Override
    public void removeCompatibility(Compatibility compatibility) {
        compatibilityDao.deleteCompatibility(compatibility);
    }
}

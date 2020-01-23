package cz.zcu.kiv.crce.apicomp.impl;

import cz.zcu.kiv.crce.apicomp.ApiCompatibilityChecker;
import cz.zcu.kiv.crce.apicomp.ApiCompatibilityCheckerService;
import cz.zcu.kiv.crce.apicomp.impl.restimpl.RestApiCompatibilityChecker;
import cz.zcu.kiv.crce.apicomp.impl.webservice.WebservicesCompatibilityChecker;
import cz.zcu.kiv.crce.apicomp.result.CompatibilityCheckResult;
import cz.zcu.kiv.crce.metadata.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A service responsible for fetching resources' metadata and passing them to
 * the correct compatibility checker.
 */
public class ApiCompatibilityCheckerServiceImpl implements ApiCompatibilityCheckerService {

    private static final Logger logger = LoggerFactory.getLogger(ApiCompatibilityCheckerServiceImpl.class);

    private List<ApiCompatibilityChecker> availableCheckers;

    public ApiCompatibilityCheckerServiceImpl() {
        availableCheckers = new ArrayList<>();
        availableCheckers.add(new RestApiCompatibilityChecker());
        availableCheckers.add(new WebservicesCompatibilityChecker());
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
    public ApiCompatibilityChecker pickChecker(Resource resource) {
        return availableCheckers.stream()
                .filter(checker -> checker.isApiSupported(resource))
                .findFirst()
                .orElse(null);
    }
}

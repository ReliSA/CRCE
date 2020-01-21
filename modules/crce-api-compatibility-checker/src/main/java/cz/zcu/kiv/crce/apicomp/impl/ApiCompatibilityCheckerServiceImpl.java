package cz.zcu.kiv.crce.apicomp.impl;

import cz.zcu.kiv.crce.apicomp.ApiCompatibilityChecker;
import cz.zcu.kiv.crce.apicomp.ApiCompatibilityCheckerService;
import cz.zcu.kiv.crce.apicomp.impl.restimpl.RestApiCompatibilityChecker;
import cz.zcu.kiv.crce.apicomp.impl.webservice.WebservicesCompatibilityChecker;
import cz.zcu.kiv.crce.apicomp.result.CompatibilityCheckResult;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Resource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A service responsible for fetching resources' metadata and passing them to
 * the correct compatibility checker.
 */
public class ApiCompatibilityCheckerServiceImpl implements ApiCompatibilityCheckerService {

    private List<ApiCompatibilityChecker> availableCheckers;

    public ApiCompatibilityCheckerServiceImpl() {
        availableCheckers = new ArrayList<>();
        availableCheckers.add(new RestApiCompatibilityChecker());
        availableCheckers.add(new WebservicesCompatibilityChecker());
    }

    @Override
    public CompatibilityCheckResult compareApis(Resource api1, Resource api2) {

        // todo: pass only resource to every layer?
        Set<Capability> metadata1 = new HashSet<>(api1.getCapabilities());
        Set<Capability> metadata2 = new HashSet<>(api2.getCapabilities());

        ApiCompatibilityChecker c1 = pickChecker(metadata1),
                c2 = pickChecker(metadata2);

        if (c1 == null || c2 == null || !c1.equals(c2)) {
            // todo: log error or something
            return null;
        }

        return c1.compareApis(metadata1, metadata2);
    }

    @Override
    public ApiCompatibilityChecker pickChecker(Resource resource) {
        Set<Capability> metadata = new HashSet<>(resource.getCapabilities());
        return availableCheckers.stream()
                .filter(checker -> checker.isApiSupported(metadata))
                .findFirst()
                .orElse(null);
    }

    @Override
    public ApiCompatibilityChecker pickChecker(Set<Capability> apiMetadata) {
        return availableCheckers.stream()
                .filter(checker -> checker.isApiSupported(apiMetadata))
                .findFirst()
                .orElse(null);
    }
}

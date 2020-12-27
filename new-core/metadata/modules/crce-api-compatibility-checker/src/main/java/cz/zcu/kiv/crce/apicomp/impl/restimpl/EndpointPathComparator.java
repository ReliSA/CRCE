package cz.zcu.kiv.crce.apicomp.impl.restimpl;

import cz.zcu.kiv.crce.apicomp.internal.DiffUtils;
import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.compatibility.DifferenceLevel;
import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.Capability;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class responsible for comparing paths to endpoints. It is possible to set a flag
 * which will cause api version to be ignored during comparison.
 *
 * E.g.:
 *  - `/rest/data/pet/{id}` and `/rest/v1/data/pet/{id}` will be evaluated as same
 *  - `/rest/v3/data/pet/{id}` and `/rest/v1/data/pet/{id}` will be evaluated as same
 */
public class EndpointPathComparator extends EndpointFeatureComparator {

    /**
     * It is assumed that version is wrapped in slashes.
     *
     * If the version is detected it is to be replaced with single slash
     *  - /rest/v1.2.3/data/pet/{id} becomes /rest/data/pet/{id}
     */
    private final static String VERSION_REGEX = "\\/[vV][0-9]+(?:[.-][0-9]+){0,2}\\/";

    private Pattern versionPattern;

    /**
     * If set, this checker tries to detect version in path to API and ignore it when picking
     * endpoint suitable for comparison.
     */
    private final boolean ignoreApiVersion;

    private Attribute<List<String>> endpoint1Paths;

    private Attribute<List<String>> endpoint2Paths;

    public EndpointPathComparator(Capability endpoint1, Capability endpoint2, boolean ignoreApiVersion) {
        super(endpoint1, endpoint2);

        endpoint1Paths = endpoint1.getAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_ENDPOINT_PATH);
        endpoint2Paths = endpoint2.getAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_ENDPOINT_PATH);
        this.ignoreApiVersion = ignoreApiVersion;

        if (this.ignoreApiVersion) {
            versionPattern = Pattern.compile(VERSION_REGEX);
        }
    }

    @Override
    public List<Diff> compare() {
        List<String> paths1 = endpoint1Paths.getValue();
        List<String> paths2 = endpoint2Paths.getValue();

        // we want at least one match

        for (int i = 0; i < paths1.size(); i++) {
            for (int j = 0; j < paths2.size(); j++) {
                String path1 = paths1.get(i),
                        path2 = paths2.get(j);

                boolean raiseMovFlag = false;
                if (ignoreApiVersion) {
                    // if paths match without version but do not match with version, raise MOV flag
                    raiseMovFlag = !path1.equals(path2);
                    path1 = detectAndRemoveApiVersion(path1);
                    path2 = detectAndRemoveApiVersion(path2);
                }

                if (path1.equals(path2)) {
                    return Collections.singletonList(DiffUtils.createDiff(endpoint1Paths.getName(), DifferenceLevel.OPERATION, Difference.NON, raiseMovFlag));
                }
            }
        }

        return Collections.singletonList(DiffUtils.createDiff(endpoint1Paths.getName(), DifferenceLevel.OPERATION, Difference.MUT));
    }

    /**
     * Tries to find one occurrence of version in API path at max and remove it.
     *
     * @param originalPath  Path to find and remove version in.
     * @return Path without version.
     */
    private String detectAndRemoveApiVersion(String originalPath) {
        Matcher m = versionPattern.matcher(originalPath);
        if (m.find()) {
            return m.replaceFirst("/");
        }

        return originalPath;
    }
}

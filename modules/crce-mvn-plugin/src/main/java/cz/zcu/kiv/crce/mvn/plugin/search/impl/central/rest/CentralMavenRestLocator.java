package cz.zcu.kiv.crce.mvn.plugin.search.impl.central.rest;

import cz.zcu.kiv.crce.mvn.plugin.search.FoundArtifact;
import cz.zcu.kiv.crce.mvn.plugin.search.MavenLocator;
import cz.zcu.kiv.crce.mvn.plugin.search.impl.VersionFilter;
import cz.zcu.kiv.crce.mvn.plugin.search.impl.central.rest.json.CentralRepoJsonResponse;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * This locator uses the central maven repo to search for artifacts - http://search.maven.org/.
 * This repo provides rest api (described here http://search.maven.org/#api) which will be used to
 * locate the artifacts.
 *
 * @author Zdenek Vales
 */
public class CentralMavenRestLocator implements MavenLocator {

    private static final Logger logger = LoggerFactory.getLogger(CentralMavenRestLocator.class);

    /**
     * Maximum number of found artifacts returned by one query.
     */
    public static final int MAX_ARTIFACTS_PER_QUERY = 50;

    private CentralRepoRestConsumer restConsumer;

    public CentralMavenRestLocator() {
        this(new CentralRepoRestConsumer());
    }

    public CentralMavenRestLocator(CentralRepoRestConsumer restConsumer) {
        this.restConsumer = restConsumer;
    }

    @Override
    public FoundArtifact locate(String groupId, String artifactId, String version) {
        logger.debug("Locating artifact.");
        QueryBuilder qb = QueryBuilder.createStandard(groupId, artifactId, version);
        int foundArtifactCount = getResultsCounts(qb);

        if(foundArtifactCount == 0) {
            return null;
        }

        return fetchResults(qb, foundArtifactCount).iterator().next();
    }

    @Override
    public Collection<FoundArtifact> locate(String groupId, String artifactId) {

        List<FoundArtifact> foundArtifacts = new ArrayList<>();

        QueryBuilder qb = new QueryBuilder()
                .addParameter(QueryParam.GROUP_ID, groupId)
                .addParameter(QueryParam.ARTIFACT_ID, artifactId)
                .addStandardAdditionalParameters();
        int foundArtifactsCount = getResultsCounts(qb);
        if(foundArtifactsCount == 0) {
            return  foundArtifacts;
        }

        return fetchResults(qb, foundArtifactsCount);
    }

    @Override
    public Collection<FoundArtifact> locate(String groupId, String artifactId, String fromVersion, String toVersion) {
        throw new UnsupportedOperationException("Sorry, not implemented yet.");
    }

    @Override
    public Collection<FoundArtifact> locate(String includedPackage, String groupIdFilter, boolean highestGroupIdMatch) {
        List<FoundArtifact> foundArtifacts = new ArrayList<>();
        int foundArtifactsCount = 0;

        // perform the first query to get the number of total results found
        QueryBuilder qb = new QueryBuilder();
        if(highestGroupIdMatch && groupIdFilter != null && !groupIdFilter.isEmpty()) {
            // add 'AND g:....' to query and get first set of results with the longest groupId possible
            String groupId = groupIdFilter;
            while (foundArtifactsCount == 0) {
                qb = new QueryBuilder()
                        .addParameter(QueryParam.CLASS_NAME, includedPackage)
                        .addParameter(QueryParam.GROUP_ID, groupId)
                        .addStandardAdditionalParameters();
                foundArtifactsCount = getResultsCounts(qb);

                String[] tmp = groupId.split("\\.");

                // groupId can't be any shorter
                if(tmp.length  <= 1) {
                    break;
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < tmp.length-1; i++) {
                    sb.append(tmp[i]).append(".");
                }
                groupId = sb.toString();
                groupId = groupId.substring(0, groupId.length()-1);
            }
        } else if (groupIdFilter != null && !groupIdFilter.isEmpty()) {
            // use manual groupId filter
            qb = new QueryBuilder()
                    .addParameter(QueryParam.CLASS_NAME, includedPackage)
                    .addParameter(QueryParam.GROUP_ID, groupIdFilter)
                    .addStandardAdditionalParameters();
            foundArtifactsCount = getResultsCounts(qb);
            if(foundArtifactsCount == 0) {
                return foundArtifacts;
            }
        } else {
            // no groupId filter
            qb = new QueryBuilder()
                    .addParameter(QueryParam.CLASS_NAME, includedPackage)
                    .addStandardAdditionalParameters();
            foundArtifactsCount = getResultsCounts(qb);
            if(foundArtifactsCount == 0) {
                return foundArtifacts;
            }
        }


        // fetch found artifacts
        return fetchResults(qb, foundArtifactsCount);
    }

    @Override
    public Collection<FoundArtifact> locate(String includedPackage, boolean highestGroupIdMatch) {
        return locate(includedPackage, includedPackage, highestGroupIdMatch);
    }

    @Override
    public Collection<FoundArtifact> locate(String includedPackage) {
        return locate(includedPackage, "", false);
    }

    @Override
    public Collection<FoundArtifact> filter(Collection<FoundArtifact> foundArtifacts, VersionFilter versionFilter) {
        switch (versionFilter) {
            case HIGHEST_ONLY:
                return getHighestVersions(foundArtifacts);
            case LOWEST_ONLY:
                return getLowestVersions(foundArtifacts);
            default:
                return foundArtifacts;
        }
    }

    /**
     * Performs the query with ROWS=0 parameter to get the total number of found results.
     *
     * @param qb QueryBuilder with prepared search criteria.
     * @return Total number of found results.
     */
    private int getResultsCounts(QueryBuilder qb) {

        qb.addAdditionalParameter(AdditionalQueryParam.ROWS,"0");
        CentralRepoJsonResponse jsonResponse = null;
        try {
            jsonResponse = restConsumer.getJson(qb);
        } catch (ServerErrorException e) {
            return 0;
        }
        int foundArtifactsCount = jsonResponse.getResponse().getNumFound();
        return foundArtifactsCount;
    }

    /**
     * Fetch results in download threads.
     *
     * @param qb QueryBuilder with prepared search criteria.
     * @param foundArtifactsCount Number of found results to be downloaded.
     * @return Downloaded results.
     */
    private Collection<FoundArtifact> fetchResults(QueryBuilder qb, int foundArtifactsCount) {
        int start = 0;
        int threadCount = Math.max(1, (int)Math.ceil((double)foundArtifactsCount / MAX_ARTIFACTS_PER_QUERY));
        List<FetchResultSetThread> threadPool = new ArrayList<>(threadCount);
        List<FoundArtifact> foundArtifacts = new ArrayList<>();

        // start downloading threads
        for (int i = 0; i < threadCount; i++) {
            logger.debug("Starting result downloading thread for "+MAX_ARTIFACTS_PER_QUERY+" results starting at "+start);
            FetchResultSetThread t = new FetchResultSetThread(qb.clone(), start, MAX_ARTIFACTS_PER_QUERY);
            threadPool.add(t);
            t.start();
            start += MAX_ARTIFACTS_PER_QUERY;
        }

        // join threads and get found artifacts
        for (FetchResultSetThread t : threadPool) {
            try {
                t.join();
                foundArtifacts.addAll(t.getFoundArtifacts());
            } catch (InterruptedException e) {
                logger.error("Error while joining result downloading thread: "+e.getMessage());
            }
        }

        return foundArtifacts;
    }

    /**
     * Return the highest versions of all found artifacts.
     * @param foundArtifacts Collection to be filtered.
     * @return Highest versions of all artifacts from foundArtifacts collection.
     */
    private Collection<FoundArtifact> getHighestVersions(Collection<FoundArtifact> foundArtifacts) {
        // group artifacts by their groupId and artifactId
        Map<String, List<FoundArtifact>> artifacts = new HashMap<>();
        for(FoundArtifact foundArtifact : foundArtifacts) {
            String name = foundArtifact.getGroupId()+":"+foundArtifact.getArtifactId();
            if(artifacts.containsKey(name)) {
                artifacts.get(name).add(foundArtifact);
            } else {
                artifacts.put(name, new ArrayList<FoundArtifact>());
                artifacts.get(name).add(foundArtifact);
            }
        }

        // select the highest version for each artifact list
        List<FoundArtifact> highestVersions = new ArrayList<>();
        for(List<FoundArtifact> tmp : artifacts.values()) {
            FoundArtifact highestVersionArt = null;
            DefaultArtifactVersion highestVersion = null;
            for(FoundArtifact fa : tmp) {
                DefaultArtifactVersion dav = new DefaultArtifactVersion(fa.getVersion());
                if(highestVersion == null) {
                    highestVersionArt = fa;
                    highestVersion = new DefaultArtifactVersion(fa.getVersion());
                } else if (highestVersion.compareTo(dav) <= 0) {
                    highestVersionArt = fa;
                    highestVersion = dav;
                }
            }
            highestVersions.add(highestVersionArt);
        }

        return highestVersions;
    }

    /**
     * Return the lowest versions of all found artifacts.
     * @param foundArtifacts Collection to be filtered.
     * @return Lowest versions of all artifacts from foundArtifacts collection.
     */
    private Collection<FoundArtifact> getLowestVersions(Collection<FoundArtifact> foundArtifacts) {
        // group artifacts by their groupId and artifactId
        Map<String, List<FoundArtifact>> artifacts = new HashMap<>();
        for(FoundArtifact foundArtifact : foundArtifacts) {
            String name = foundArtifact.getGroupId()+":"+foundArtifact.getArtifactId();
            if(artifacts.containsKey(name)) {
                artifacts.get(name).add(foundArtifact);
            } else {
                artifacts.put(name, new ArrayList<FoundArtifact>());
                artifacts.get(name).add(foundArtifact);
            }
        }

        // select the lowest version for each artifact list
        List<FoundArtifact> lowestVersions = new ArrayList<>();
        for(List<FoundArtifact> tmp : artifacts.values()) {
            FoundArtifact lowestVersionArt = null;
            DefaultArtifactVersion lowestVersion = null;
            for(FoundArtifact fa : tmp) {
                DefaultArtifactVersion dav = new DefaultArtifactVersion(fa.getVersion());
                if(lowestVersion == null) {
                    lowestVersionArt = fa;
                    lowestVersion = new DefaultArtifactVersion(fa.getVersion());
                } else if (lowestVersion.compareTo(dav) >= 0) {
                    lowestVersionArt = fa;
                    lowestVersion = dav;
                }
            }
            lowestVersions.add(lowestVersionArt);
        }

        return lowestVersions;
    }
}

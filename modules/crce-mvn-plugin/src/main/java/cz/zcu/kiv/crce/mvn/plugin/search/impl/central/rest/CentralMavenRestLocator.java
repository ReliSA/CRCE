package cz.zcu.kiv.crce.mvn.plugin.search.impl.central.rest;

import cz.zcu.kiv.crce.mvn.plugin.search.FoundArtifact;
import cz.zcu.kiv.crce.mvn.plugin.search.MavenLocator;
import cz.zcu.kiv.crce.mvn.plugin.search.impl.SimpleFoundArtifact;
import cz.zcu.kiv.crce.mvn.plugin.search.impl.VersionFilter;
import cz.zcu.kiv.crce.mvn.plugin.search.impl.central.rest.json.CentralRepoJsonResponse;
import cz.zcu.kiv.crce.mvn.plugin.search.impl.central.rest.json.JsonArtifactDescriptor;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
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
        restConsumer = new CentralRepoRestConsumer();
    }

    public CentralMavenRestLocator(CentralRepoRestConsumer restConsumer) {
        this.restConsumer = restConsumer;
    }

    @Override
    public FoundArtifact locate(String groupId, String artifactId, String version) {
        logger.debug("Locating artifact.");
        QueryBuilder qb = QueryBuilder.createStandard(groupId, artifactId, version);
        CentralRepoJsonResponse jsonResponse = restConsumer.getJson(qb);

        if(jsonResponse.getResponse().getNumFound() == 0 ) {
            // no artifact found
            return null;
        }

        // todo: maybe use a dozer for this?
        // convert the found artifact
        JsonArtifactDescriptor artifactDescriptor = jsonResponse.getResponse().getDocs()[0];
        return new SimpleFoundArtifact(artifactDescriptor.getG(),
                artifactDescriptor.getA(),
                artifactDescriptor.getV(),
                artifactDescriptor.jarDownloadLink(),
                artifactDescriptor.pomDownloadLink());
    }

    @Override
    public Collection<FoundArtifact> locate(String groupId, String artifactId) {

        List<FoundArtifact> foundArtifacts = new ArrayList<>();

        QueryBuilder qb = new QueryBuilder()
                .addParameter(QueryParam.GROUP_ID, groupId)
                .addParameter(QueryParam.ARTIFACT_ID, artifactId)
                .addStandardAdditionalParameters();
        CentralRepoJsonResponse jsonResponse = restConsumer.getJson(qb);

        if(jsonResponse.getResponse().getNumFound() == 0 ) {
            // no artifact found
            return foundArtifacts;
        }

        // todo: maybe use dozer for this?
        // convert the found artifacts
        for(JsonArtifactDescriptor ad : jsonResponse.getResponse().getDocs()) {
            foundArtifacts.add(new SimpleFoundArtifact(ad.getG(),
                    ad.getA(),
                    ad.getV(),
                    ad.jarDownloadLink(),
                    ad.pomDownloadLink()));
        }

        return foundArtifacts;
    }

    @Override
    public Collection<FoundArtifact> locate(String groupId, String artifactId, String fromVersion, String toVersion) {
        throw new UnsupportedOperationException("Sorry, not implemented yet.");
    }

    @Override
    public Collection<FoundArtifact> locate(String includedPackage) {
        List<FoundArtifact> foundArtifacts = new ArrayList<>();
        List<JsonArtifactDescriptor> jsonArtifactDescriptors = new ArrayList<>();
        int foundArtifactsCount = 0;

        // perform the first query to get the number of total results found
        QueryBuilder qb = new QueryBuilder()
                .addParameter(QueryParam.CLASS_NAME, includedPackage)
                .addStandardAdditionalParameters()
                .addAdditionalParameter(AdditionalQueryParam.ROWS,"0");
        CentralRepoJsonResponse jsonResponse = restConsumer.getJson(qb);
        foundArtifactsCount = jsonResponse.getResponse().getNumFound();
        if(foundArtifactsCount == 0 ) {
            // no artifact found
            return foundArtifacts;
        }


        // fetch found artifacts
        // fetch only MAX_ARTIFACTS_PER_QUERY per 1 query
        int start = 0;
        qb = qb.addAdditionalParameter(AdditionalQueryParam.ROWS, Integer.toString(MAX_ARTIFACTS_PER_QUERY));
        while(start < foundArtifactsCount) {
            // set new start
            qb = qb.addAdditionalParameter(AdditionalQueryParam.START, Integer.toString(start));
            jsonResponse = restConsumer.getJson(qb);
            jsonArtifactDescriptors.addAll(Arrays.asList(jsonResponse.getResponse().getDocs()));
            start = Math.min(foundArtifactsCount, start+MAX_ARTIFACTS_PER_QUERY);
        }

        // todo: maybe use dozer for this?
        // convert the found artifacts
        for(JsonArtifactDescriptor ad : jsonArtifactDescriptors) {
            foundArtifacts.add(new SimpleFoundArtifact(ad.getG(),
                    ad.getA(),
                    ad.getV(),
                    ad.jarDownloadLink(),
                    ad.pomDownloadLink()));
        }

        return foundArtifacts;
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
     * Return the highest versions of all found artifacts.
     * @param foundArtifacts
     * @return
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
     * @param foundArtifacts
     * @return
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

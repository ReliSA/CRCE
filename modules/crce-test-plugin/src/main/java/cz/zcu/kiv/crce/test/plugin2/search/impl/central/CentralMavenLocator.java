package cz.zcu.kiv.crce.test.plugin2.search.impl.central;

import cz.zcu.kiv.crce.test.plugin2.search.FoundArtifact;
import cz.zcu.kiv.crce.test.plugin2.search.MavenLocator;
import cz.zcu.kiv.crce.test.plugin2.search.impl.central.json.CentralRepoJsonResponse;
import cz.zcu.kiv.crce.test.plugin2.search.impl.central.json.JsonArtifactDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This locator uses the central maven repo to search for artifacts - http://search.maven.org/.
 * This repo provides rest api (described here http://search.maven.org/#api) which will be used to
 * locate the artifacts.
 *
 * @author Zdenek Vales
 */
public class CentralMavenLocator implements MavenLocator {

    private static final Logger logger = LoggerFactory.getLogger(CentralMavenLocator.class);

    private CentralRepoRestConsumer restConsumer;

    public CentralMavenLocator() {
        restConsumer = new CentralRepoRestConsumer();
    }

    public CentralMavenLocator(CentralRepoRestConsumer restConsumer) {
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

        // todo: maybe use a dozer for this?
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
}

package cz.zcu.kiv.crce.search.impl.central;

import cz.zcu.kiv.crce.search.FoundArtifact;
import cz.zcu.kiv.crce.search.MavenLocator;
import cz.zcu.kiv.crce.search.impl.central.json.CentralRepoJsonResponse;
import cz.zcu.kiv.crce.search.impl.central.json.JsonArtifactDescriptor;

import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

/**
 * This locator uses the central maven repo to search for artifacts - http://search.maven.org/.
 * This repo provides rest api (described here http://search.maven.org/#api) which will be used to
 * locate the artifacts.
 *
 * @author Zdenek Vales
 */
public class CentralMavenLocator implements MavenLocator {

    private CentralRepoRestConsumer restConsumer;

    public CentralMavenLocator() {
        restConsumer = new CentralRepoRestConsumer();
    }

    public CentralMavenLocator(CentralRepoRestConsumer restConsumer) {
        this.restConsumer = restConsumer;
    }

    @Override
    public FoundArtifact locate(String groupId, String artifactId, String version) {


        // todo: move this part of code to the separate method / class
        QueryBuilder qb = new QueryBuilder()
                .addParameter(QueryParam.GROUP_ID, groupId)
                .addParameter(QueryParam.ARTIFACT_ID, artifactId)
                .addParameter(QueryParam.VERSION, version)
                .addStandardAdditionalParameters();
        Response response = restConsumer.sendRequest(qb);

        if(response.getStatusInfo().getStatusCode() != Response.Status.OK.getStatusCode()) {
            // no artifact found
            return null;
        }
        CentralRepoJsonResponse jsonResponse = response.readEntity(CentralRepoJsonResponse.class);

        if(jsonResponse.getResponse().getNumFound() == 0 ) {
            // no artifact found
            return null;
        }

        // convert the found artifact
        final JsonArtifactDescriptor artifactDescriptor = jsonResponse.getResponse().getDocs()[0];

        return new FoundArtifact() {
            @Override
            public String getGroupId() {
                return artifactDescriptor.getG();
            }

            @Override
            public String getArtifactId() {
                return artifactDescriptor.getA();
            }

            @Override
            public String getVersion() {
                return artifactDescriptor.getV();
            }

            @Override
            public URL getPomDownloadLink() {
                return null;
            }

            @Override
            public URL getJarDownloadLink() {
                try {
                    return new URL(artifactDescriptor.jarDownloadLink());
                } catch (MalformedURLException e) {
                    // error occured
                    e.printStackTrace();
                }

                return null;
            }
        };
    }

    @Override
    public Collection<FoundArtifact> locate(String groupId, String artifactId) {
        return null;
    }
}

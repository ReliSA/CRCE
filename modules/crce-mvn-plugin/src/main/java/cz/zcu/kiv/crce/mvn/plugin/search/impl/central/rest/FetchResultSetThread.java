package cz.zcu.kiv.crce.mvn.plugin.search.impl.central.rest;

import cz.zcu.kiv.crce.mvn.plugin.search.FoundArtifact;
import cz.zcu.kiv.crce.mvn.plugin.search.impl.SimpleFoundArtifact;
import cz.zcu.kiv.crce.mvn.plugin.search.impl.central.rest.json.CentralRepoJsonResponse;
import cz.zcu.kiv.crce.mvn.plugin.search.impl.central.rest.json.JsonArtifactDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Downloads and parses a set of search results.
 *
 * @author Zdenek Vales
 */
public class FetchResultSetThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(FetchResultSetThread.class.getName());

    private QueryBuilder queryBuilder;

    private List<FoundArtifact> foundArtifacts;

    /**
     * Constructor.
     * @param queryBuilder Query builder containing the original query. The object will be changed in constructor. Use the clone() method.
     * @param start Number of the starting result.
     * @param rows How many results should be fetched.
     */
    public FetchResultSetThread(QueryBuilder queryBuilder, int start, int rows) {
        this.queryBuilder = queryBuilder;
        foundArtifacts = new ArrayList<>();
        this.queryBuilder = this.queryBuilder.addAdditionalParameter(AdditionalQueryParam.ROWS, Integer.toString(rows));
        this.queryBuilder = this.queryBuilder.addAdditionalParameter(AdditionalQueryParam.START, Integer.toString(start));
    }

    /**
     * Constructor.
     * Query builder is not modified (start and rows parameters are expected to be already set).
     *
     * @param queryBuilder
     */
    public FetchResultSetThread(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    @Override
    public void run() {
        // download results
        List<JsonArtifactDescriptor> jsonArtifactDescriptors = new ArrayList<>();
        List<FoundArtifact> foundArtifactsTmp = new ArrayList<>();
        CentralRepoRestConsumer restConsumer = new CentralRepoRestConsumer();
        CentralRepoJsonResponse jsonResponse = null;
        try {
            jsonResponse = restConsumer.getJson(queryBuilder);
            if(jsonResponse.getResponse().getNumFound() > 0) {
                jsonArtifactDescriptors.addAll(Arrays.asList(jsonResponse.getResponse().getDocs()));
            }
        } catch (ServerErrorException e) {
            // error
            logger.error("Exception while downloading a json response: {}.", e.getMessage());
        }

        // parse results
        for(JsonArtifactDescriptor ad : jsonArtifactDescriptors) {
            foundArtifactsTmp.add(new SimpleFoundArtifact(ad.getG(),
                    ad.getA(),
                    ad.getV(),
                    ad.jarDownloadLink(),
                    ad.pomDownloadLink()));
        }
        setFoundArtifacts(foundArtifactsTmp);
    }

    public synchronized void setFoundArtifacts(List<FoundArtifact> foundArtifacts) {
        this.foundArtifacts = foundArtifacts;
    }

    public synchronized List<FoundArtifact> getFoundArtifacts() {
        return foundArtifacts;
    }
}

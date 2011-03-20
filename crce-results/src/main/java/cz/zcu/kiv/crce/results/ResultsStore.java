package cz.zcu.kiv.crce.results;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.plugin.Plugin;
import java.io.IOException;
import java.net.URI;

/**
 * This interface defines the store for results of tests launched on resources.
 * 
 * <p>If some plugin generates a result sets up a capability or a requirement to
 * the resource, it can save the corresponding results file to this store. The
 * store automatically marks these capabilities or resources in such way so it
 * could later find the results file for requested capabilities or requirements.
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public interface ResultsStore {

    /**
     * Stores the result file of the test and associates it with corresponding
     * capabilities.
     * 
     * @param resource the resource that the test ran on.
     * @param capabilities one or more capabilities set up by the test to the
     * resource.
     * @param resultsFile an URI to the file containing results of the test.
     * @param provider plugin that generated the results and capabilities.
     * @return the created <code>Result</code>.
     * @throws IOException  
     */
    Result storeResult(Plugin provider, Resource resource, URI resultsFile, Capability... capabilities) throws IOException;

    /**
     * Stores the result file of the test and associates it with corresponding
     * requirements.
     * 
     * @param resource the resource that the test ran on.
     * @param requirements one or more requirements set up by the test to the
     * resource.
     * @param resultsFile an URI to the file containing results of the test.
     * @param provider plugin that generated the results and capabilities.
     * @return the created <code>Result</code>.
     * @throws IOException  
     */
    Result storeResult(Plugin provider, Resource resource, URI resultsFile, Requirement... requirements) throws IOException;

    /**
     * Associates the result to one or more capabilities.
     * 
     * <p>It can be useful to associate the result with capabilities if it was
     * previously stored in association with requirement.
     * @param result the result which has to be associated with capabilities.
     * @param capabilities one or more capabilities to be associated with the
     * result.
     * @return the created result.
     */
    Result associateResult(Result result, Capability... capabilities);
    
    /**
     * Associates the result to one or more requirements.
     * 
     * <p>It can be useful to associate the result with requirements if it was
     * previously stored in association with capability.
     * @param result the result which has to be associated with capabilities.
     * @param requirements one or more requirements to be associated with the
     * result.
     * @return the created result.
     */
    Result associateResult(Result result, Requirement... requirements);
    
    Result getResult(Resource resource, Capability capability);
    
    
    Result getResult(Resource resource, Requirement requirement);
    
    Capability[] getCapabilities(Result plugin, Resource resource);
    
    Result getProvider(Resource resource, Capability capability);
    
}

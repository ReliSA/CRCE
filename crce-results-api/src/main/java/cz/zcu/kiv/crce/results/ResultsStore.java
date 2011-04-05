package cz.zcu.kiv.crce.results;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.plugin.Plugin;
import java.io.IOException;
import java.net.URI;
import java.util.List;
/*
 * Zmeny od schuzky 2011-03-24:
 * Aby sly vytvorit M:N vazby mezi Capabilities/Requirements a Results,
 * a zaroven aby bylo mozno ukladat vysledky prubezne bez nastavovani Capabilities
 * a Requirements (nebo naopak pri kazdem ulozeni vysledku se muze nejaka Cap./Req.
 * napr. upresnit), ukladani vysledku a jejich asociace s Cap./Req. je ve dvou
 * krocich:
 * 1) ulozeni vysledku do uloziste + ziskani reference na objekt Result
 * 2) asociace vysledku (Result) s Cap./Req. (pro M:N asociovat kazdou vazbu zvlast)
 */
/**
 * This interface defines the store for results of tests launched on resources.
 * 
 * <p>Every result is associated to the plugin that has generated the result and
 * with a the resource that the test was generated for.
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
     * @param resultsFile an URI to the file containing results of the test.
     * @param provider plugin that generated the results and capabilities.
     * @return the created <code>Result</code>.
     * @throws IOException  
     */
    Result storeResult(Resource resource, URI resultsFile, Plugin provider) throws IOException;
    
    /**
     * Associates the results to the capability.
     * 
     * <p>It can be useful to associate the result with capabilities if it was
     * previously stored in association with requirement.
     * @param result the result which has to be associated with capabilities.
     * @param capability one or more capabilities to be associated with the
     * result.
     * @return the created result.
     */
    Result associateCapability(Result result, Capability capability);
    
    /**
     * Associates the results to the requirement.
     * 
     * <p>It can be useful to associate the result with requirements if it was
     * previously stored in association with capability.
     * @param result the result which has to be associated with capabilities.
     * @param requirement one or more requirements to be associated with the
     * result.
     * @return the created result.
     */
    Result associateRequirements(Result result, Requirement requirement);
    
    /**
     * Returns all results created by the given plugin for the given resource.
     * @param plugin
     * @param resource
     * @return 
     */
    List<Result> getResults(Resource resource, Plugin plugin);
    
    /**
     * Returns all results created by the given plugin for the given resource.
     * 
     * <p>If <code>allVersions</code> is <code>true</code>, the method returns
     * also all results created by previous versions of Plugin.
     * 
     * <i>If needed, an overloading method with a version range could be created.</i>
     * 
     * @param resource
     * @param plugin
     * @param allVersions
     * @return  
     */
    List<Result> getResults(Resource resource, Plugin plugin, boolean allVersions);
    
    List<Result> getResults(Resource resource, Capability capability);
    
    List<Result> getResults(Resource resource, Requirement requirement);
    
    Result getProvider(Resource resource, Capability capability);
    
}

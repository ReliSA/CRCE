package cz.zcu.kiv.crce.plugin;

import cz.zcu.kiv.crce.metadata.Resource;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Resource indexer indexes content of an artifact and store obtained metadata
 * into <code>Resource</code> object.
 * 
 * <p> Indexer can set up one or more categories of resource (e.g. 'jar' for JAR
 * files, 'jpeg' for JPEG files etc.) which can ease the resources discovery.
 * 
 * <p> Indexing process may be optimized by hierarchizing indexers in dependence
 * on categories that they provides or categories that they require to index
 * a resource. E.g. root indexer could set up file type (like 'jar', 'jpeg',
 * 'txt'...) as category, some other indexer which requires 'jar' category could
 * set up 'osgi' or 'cosi' as categories.
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public interface ResourceIndexer extends Plugin {
    
    /**
     * Indexes artifact data from given <code>InputStream</code> and stores
     * results into new <code>Resource</code> instance.
     * 
     * <p> If indexer can't index artifact content, an empty resource (with no
     * capabilities, requirements or categories) is returned.
     * 
     * @param input
     * @return 
     */
    Resource index(InputStream input);
    
    /**
     * Indexes data from given <code>InputStream</code> and stores results into
     * given <code>Resource</code> object.
     * 
     * <p> If given resource is <code>null</code>, then the new instance of
     * <code>Resource</code> is created.
     * 
     * <p> If indexer can't index artifact content, resource stays unchanged.
     * 
     * @param input
     * @param resource
     * @return 
     */
    Resource index(InputStream input, Resource resource);
    
    /**
     * Returns set of categories that this indexer can discover from an indexed
     * artifact.
     * @return 
     */
    String[] getProvidedCategories();

    /**
     * Returns set of categories that this indexer requires to be present in an
     * indexed artifact.
     * @return 
     */
    String[] getRequiredCategories();
}
